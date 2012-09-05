package org.jsoup.parser;

/**
 * States and transition activations for the Tokeniser.
 */
enum TokeniserState {
    Data {
        // in data state, gather characters until a character reference or tag
        // is found
        @Override
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
            case '&':
                t.advanceTransition(CharacterReferenceInData);
                break;
            case '<':
                t.advanceTransition(TagOpen);
                break;
            case nullChar:
                t.error(this); // NOT replacement character (oddly?)
                t.emit(r.consume());
                break;
            case eof:
                t.emit(new Token.EOF());
                break;
            default:
                String data = r.consumeToAny('&', '<', nullChar);
                t.emit(data);
                break;
            }
        }
    },
    CharacterReferenceInData {
        // from & in data
        @Override
        void read(Tokeniser t, CharacterReader r) {
            Character c = t.consumeCharacterReference(null, false);
            if (c == null) {
                t.emit('&');
            } else {
                t.emit(c);
            }
            t.transition(Data);
        }
    },
    Rcdata {
        // / handles data in title, textarea etc
        @Override
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
            case '&':
                t.advanceTransition(CharacterReferenceInRcdata);
                break;
            case '<':
                t.advanceTransition(RcdataLessthanSign);
                break;
            case nullChar:
                t.error(this);
                r.advance();
                t.emit(replacementChar);
                break;
            case eof:
                t.emit(new Token.EOF());
                break;
            default:
                String data = r.consumeToAny('&', '<', nullChar);
                t.emit(data);
                break;
            }
        }
    },
    CharacterReferenceInRcdata {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            Character c = t.consumeCharacterReference(null, false);
            if (c == null) {
                t.emit('&');
            } else {
                t.emit(c);
            }
            t.transition(Rcdata);
        }
    },
    Rawtext {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
            case '<':
                t.advanceTransition(RawtextLessthanSign);
                break;
            case nullChar:
                t.error(this);
                r.advance();
                t.emit(replacementChar);
                break;
            case eof:
                t.emit(new Token.EOF());
                break;
            default:
                String data = r.consumeToAny('<', nullChar);
                t.emit(data);
                break;
            }
        }
    },
    ScriptData {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
            case '<':
                t.advanceTransition(ScriptDataLessthanSign);
                break;
            case nullChar:
                t.error(this);
                r.advance();
                t.emit(replacementChar);
                break;
            case eof:
                t.emit(new Token.EOF());
                break;
            default:
                String data = r.consumeToAny('<', nullChar);
                t.emit(data);
                break;
            }
        }
    },
    PLAINTEXT {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
            case nullChar:
                t.error(this);
                r.advance();
                t.emit(replacementChar);
                break;
            case eof:
                t.emit(new Token.EOF());
                break;
            default:
                String data = r.consumeTo(nullChar);
                t.emit(data);
                break;
            }
        }
    },
    TagOpen {
        // from < in data
        @Override
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
            case '!':
                t.advanceTransition(MarkupDeclarationOpen);
                break;
            case '/':
                t.advanceTransition(EndTagOpen);
                break;
            case '?':
                t.advanceTransition(BogusComment);
                break;
            default:
                if (r.matchesLetter()) {
                    t.createTagPending(true);
                    t.transition(TagName);
                } else {
                    t.error(this);
                    t.emit('<'); // char that got us here
                    t.transition(Data);
                }
                break;
            }
        }
    },
    EndTagOpen {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.emit("</");
                t.transition(Data);
            } else if (r.matchesLetter()) {
                t.createTagPending(false);
                t.transition(TagName);
            } else if (r.matches('>')) {
                t.error(this);
                t.advanceTransition(Data);
            } else {
                t.error(this);
                t.advanceTransition(BogusComment);
            }
        }
    },
    TagName {
        // from < or </ in data, will have start or end tag pending
        @Override
        void read(Tokeniser t, CharacterReader r) {
            // previous TagOpen state did NOT consume, will have a letter char
            // in current
            String tagName = r.consumeToAny('\t', '\n', '\f', ' ', '/', '>',
                    nullChar).toLowerCase();
            t.tagPending.appendTagName(tagName);

            switch (r.consume()) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                t.transition(BeforeAttributeName);
                break;
            case '/':
                t.transition(SelfClosingStartTag);
                break;
            case '>':
                t.emitTagPending();
                t.transition(Data);
                break;
            case nullChar: // replacement
                t.tagPending.appendTagName(replacementStr);
                break;
            case eof: // should emit pending tag?
                t.eofError(this);
                t.transition(Data);
                // no default, as covered with above consumeToAny
            }
        }
    },
    RcdataLessthanSign {
        // from < in rcdata
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('/')) {
                t.createTempBuffer();
                t.advanceTransition(RCDATAEndTagOpen);
            } else if (r.matchesLetter()
                    && !r.containsIgnoreCase("</" + t.appropriateEndTagName())) {
                // diverge from spec: got a start tag, but there's no
                // appropriate end tag (</title>), so rather than
                // consuming to EOF; break out here
                t.tagPending = new Token.EndTag(t.appropriateEndTagName());
                t.emitTagPending();
                r.unconsume(); // undo "<"
                t.transition(Data);
            } else {
                t.emit("<");
                t.transition(Rcdata);
            }
        }
    },
    RCDATAEndTagOpen {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.tagPending.appendTagName(Character.toLowerCase(r.current()));
                t.dataBuffer.append(Character.toLowerCase(r.current()));
                t.advanceTransition(RCDATAEndTagName);
            } else {
                t.emit("</");
                t.transition(Rcdata);
            }
        }
    },
    RCDATAEndTagName {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                String name = r.consumeLetterSequence();
                t.tagPending.appendTagName(name.toLowerCase());
                t.dataBuffer.append(name);
                return;
            }

            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                if (t.isAppropriateEndTagToken()) {
                    t.transition(BeforeAttributeName);
                } else {
                    anythingElse(t, r);
                }
                break;
            case '/':
                if (t.isAppropriateEndTagToken()) {
                    t.transition(SelfClosingStartTag);
                } else {
                    anythingElse(t, r);
                }
                break;
            case '>':
                if (t.isAppropriateEndTagToken()) {
                    t.emitTagPending();
                    t.transition(Data);
                } else {
                    anythingElse(t, r);
                }
                break;
            default:
                anythingElse(t, r);
            }
        }

        private void anythingElse(Tokeniser t, CharacterReader r) {
            t.emit("</" + t.dataBuffer.toString());
            t.transition(Rcdata);
        }
    },
    RawtextLessthanSign {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('/')) {
                t.createTempBuffer();
                t.advanceTransition(RawtextEndTagOpen);
            } else {
                t.emit('<');
                t.transition(Rawtext);
            }
        }
    },
    RawtextEndTagOpen {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.transition(RawtextEndTagName);
            } else {
                t.emit("</");
                t.transition(Rawtext);
            }
        }
    },
    RawtextEndTagName {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                String name = r.consumeLetterSequence();
                t.tagPending.appendTagName(name.toLowerCase());
                t.dataBuffer.append(name);
                return;
            }

            if (t.isAppropriateEndTagToken() && !r.isEmpty()) {
                char c = r.consume();
                switch (c) {
                case '\t':
                case '\n':
                case '\f':
                case ' ':
                    t.transition(BeforeAttributeName);
                    break;
                case '/':
                    t.transition(SelfClosingStartTag);
                    break;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                default:
                    t.dataBuffer.append(c);
                    anythingElse(t, r);
                }
            } else {
                anythingElse(t, r);
            }
        }

        private void anythingElse(Tokeniser t, CharacterReader r) {
            t.emit("</" + t.dataBuffer.toString());
            t.transition(Rawtext);
        }
    },
    ScriptDataLessthanSign {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
            case '/':
                t.createTempBuffer();
                t.transition(ScriptDataEndTagOpen);
                break;
            case '!':
                t.emit("<!");
                t.transition(ScriptDataEscapeStart);
                break;
            default:
                t.emit("<");
                r.unconsume();
                t.transition(ScriptData);
            }
        }
    },
    ScriptDataEndTagOpen {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.transition(ScriptDataEndTagName);
            } else {
                t.emit("</");
                t.transition(ScriptData);
            }

        }
    },
    ScriptDataEndTagName {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                String name = r.consumeLetterSequence();
                t.tagPending.appendTagName(name.toLowerCase());
                t.dataBuffer.append(name);
                return;
            }

            if (t.isAppropriateEndTagToken() && !r.isEmpty()) {
                char c = r.consume();
                switch (c) {
                case '\t':
                case '\n':
                case '\f':
                case ' ':
                    t.transition(BeforeAttributeName);
                    break;
                case '/':
                    t.transition(SelfClosingStartTag);
                    break;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                default:
                    t.dataBuffer.append(c);
                    anythingElse(t, r);
                }
            } else {
                anythingElse(t, r);
            }
        }

        private void anythingElse(Tokeniser t, CharacterReader r) {
            t.emit("</" + t.dataBuffer.toString());
            t.transition(ScriptData);
        }
    },
    ScriptDataEscapeStart {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('-')) {
                t.emit('-');
                t.advanceTransition(ScriptDataEscapeStartDash);
            } else {
                t.transition(ScriptData);
            }
        }
    },
    ScriptDataEscapeStartDash {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('-')) {
                t.emit('-');
                t.advanceTransition(ScriptDataEscapedDashDash);
            } else {
                t.transition(ScriptData);
            }
        }
    },
    ScriptDataEscaped {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }

            switch (r.current()) {
            case '-':
                t.emit('-');
                t.advanceTransition(ScriptDataEscapedDash);
                break;
            case '<':
                t.advanceTransition(ScriptDataEscapedLessthanSign);
                break;
            case nullChar:
                t.error(this);
                r.advance();
                t.emit(replacementChar);
                break;
            default:
                String data = r.consumeToAny('-', '<', nullChar);
                t.emit(data);
            }
        }
    },
    ScriptDataEscapedDash {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }

            char c = r.consume();
            switch (c) {
            case '-':
                t.emit(c);
                t.transition(ScriptDataEscapedDashDash);
                break;
            case '<':
                t.transition(ScriptDataEscapedLessthanSign);
                break;
            case nullChar:
                t.error(this);
                t.emit(replacementChar);
                t.transition(ScriptDataEscaped);
                break;
            default:
                t.emit(c);
                t.transition(ScriptDataEscaped);
            }
        }
    },
    ScriptDataEscapedDashDash {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }

            char c = r.consume();
            switch (c) {
            case '-':
                t.emit(c);
                break;
            case '<':
                t.transition(ScriptDataEscapedLessthanSign);
                break;
            case '>':
                t.emit(c);
                t.transition(ScriptData);
                break;
            case nullChar:
                t.error(this);
                t.emit(replacementChar);
                t.transition(ScriptDataEscaped);
                break;
            default:
                t.emit(c);
                t.transition(ScriptDataEscaped);
            }
        }
    },
    ScriptDataEscapedLessthanSign {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTempBuffer();
                t.dataBuffer.append(Character.toLowerCase(r.current()));
                t.emit("<" + r.current());
                t.advanceTransition(ScriptDataDoubleEscapeStart);
            } else if (r.matches('/')) {
                t.createTempBuffer();
                t.advanceTransition(ScriptDataEscapedEndTagOpen);
            } else {
                t.emit('<');
                t.transition(ScriptDataEscaped);
            }
        }
    },
    ScriptDataEscapedEndTagOpen {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.tagPending.appendTagName(Character.toLowerCase(r.current()));
                t.dataBuffer.append(r.current());
                t.advanceTransition(ScriptDataEscapedEndTagName);
            } else {
                t.emit("</");
                t.transition(ScriptDataEscaped);
            }
        }
    },
    ScriptDataEscapedEndTagName {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                String name = r.consumeLetterSequence();
                t.tagPending.appendTagName(name.toLowerCase());
                t.dataBuffer.append(name);
                return;
            }

            if (t.isAppropriateEndTagToken() && !r.isEmpty()) {
                char c = r.consume();
                switch (c) {
                case '\t':
                case '\n':
                case '\f':
                case ' ':
                    t.transition(BeforeAttributeName);
                    break;
                case '/':
                    t.transition(SelfClosingStartTag);
                    break;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                default:
                    t.dataBuffer.append(c);
                    anythingElse(t, r);
                    break;
                }
            } else {
                anythingElse(t, r);
            }
        }

        private void anythingElse(Tokeniser t, CharacterReader r) {
            t.emit("</" + t.dataBuffer.toString());
            t.transition(ScriptDataEscaped);
        }
    },
    ScriptDataDoubleEscapeStart {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                String name = r.consumeLetterSequence();
                t.dataBuffer.append(name.toLowerCase());
                t.emit(name);
                return;
            }

            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
            case '/':
            case '>':
                if (t.dataBuffer.toString().equals("script")) {
                    t.transition(ScriptDataDoubleEscaped);
                } else {
                    t.transition(ScriptDataEscaped);
                }
                t.emit(c);
                break;
            default:
                r.unconsume();
                t.transition(ScriptDataEscaped);
            }
        }
    },
    ScriptDataDoubleEscaped {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.current();
            switch (c) {
            case '-':
                t.emit(c);
                t.advanceTransition(ScriptDataDoubleEscapedDash);
                break;
            case '<':
                t.emit(c);
                t.advanceTransition(ScriptDataDoubleEscapedLessthanSign);
                break;
            case nullChar:
                t.error(this);
                r.advance();
                t.emit(replacementChar);
                break;
            case eof:
                t.eofError(this);
                t.transition(Data);
                break;
            default:
                String data = r.consumeToAny('-', '<', nullChar);
                t.emit(data);
            }
        }
    },
    ScriptDataDoubleEscapedDash {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '-':
                t.emit(c);
                t.transition(ScriptDataDoubleEscapedDashDash);
                break;
            case '<':
                t.emit(c);
                t.transition(ScriptDataDoubleEscapedLessthanSign);
                break;
            case nullChar:
                t.error(this);
                t.emit(replacementChar);
                t.transition(ScriptDataDoubleEscaped);
                break;
            case eof:
                t.eofError(this);
                t.transition(Data);
                break;
            default:
                t.emit(c);
                t.transition(ScriptDataDoubleEscaped);
            }
        }
    },
    ScriptDataDoubleEscapedDashDash {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '-':
                t.emit(c);
                break;
            case '<':
                t.emit(c);
                t.transition(ScriptDataDoubleEscapedLessthanSign);
                break;
            case '>':
                t.emit(c);
                t.transition(ScriptData);
                break;
            case nullChar:
                t.error(this);
                t.emit(replacementChar);
                t.transition(ScriptDataDoubleEscaped);
                break;
            case eof:
                t.eofError(this);
                t.transition(Data);
                break;
            default:
                t.emit(c);
                t.transition(ScriptDataDoubleEscaped);
            }
        }
    },
    ScriptDataDoubleEscapedLessthanSign {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('/')) {
                t.emit('/');
                t.createTempBuffer();
                t.advanceTransition(ScriptDataDoubleEscapeEnd);
            } else {
                t.transition(ScriptDataDoubleEscaped);
            }
        }
    },
    ScriptDataDoubleEscapeEnd {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                String name = r.consumeLetterSequence();
                t.dataBuffer.append(name.toLowerCase());
                t.emit(name);
                return;
            }

            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
            case '/':
            case '>':
                if (t.dataBuffer.toString().equals("script")) {
                    t.transition(ScriptDataEscaped);
                } else {
                    t.transition(ScriptDataDoubleEscaped);
                }
                t.emit(c);
                break;
            default:
                r.unconsume();
                t.transition(ScriptDataDoubleEscaped);
            }
        }
    },
    BeforeAttributeName {
        // from tagname <xxx
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                break; // ignore whitespace
            case '/':
                t.transition(SelfClosingStartTag);
                break;
            case '>':
                t.emitTagPending();
                t.transition(Data);
                break;
            case nullChar:
                t.error(this);
                t.tagPending.newAttribute();
                r.unconsume();
                t.transition(AttributeName);
                break;
            case eof:
                t.eofError(this);
                t.transition(Data);
                break;
            case '"':
            case '\'':
            case '<':
            case '=':
                t.error(this);
                t.tagPending.newAttribute();
                t.tagPending.appendAttributeName(c);
                t.transition(AttributeName);
                break;
            default: // A-Z, anything else
                t.tagPending.newAttribute();
                r.unconsume();
                t.transition(AttributeName);
            }
        }
    },
    AttributeName {
        // from before attribute name
        @Override
        void read(Tokeniser t, CharacterReader r) {
            String name = r.consumeToAny('\t', '\n', '\f', ' ', '/', '=', '>',
                    nullChar, '"', '\'', '<');
            t.tagPending.appendAttributeName(name.toLowerCase());

            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                t.transition(AfterAttributeName);
                break;
            case '/':
                t.transition(SelfClosingStartTag);
                break;
            case '=':
                t.transition(BeforeAttributeValue);
                break;
            case '>':
                t.emitTagPending();
                t.transition(Data);
                break;
            case nullChar:
                t.error(this);
                t.tagPending.appendAttributeName(replacementChar);
                break;
            case eof:
                t.eofError(this);
                t.transition(Data);
                break;
            case '"':
            case '\'':
            case '<':
                t.error(this);
                t.tagPending.appendAttributeName(c);
                // no default, as covered in consumeToAny
            }
        }
    },
    AfterAttributeName {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                // ignore
                break;
            case '/':
                t.transition(SelfClosingStartTag);
                break;
            case '=':
                t.transition(BeforeAttributeValue);
                break;
            case '>':
                t.emitTagPending();
                t.transition(Data);
                break;
            case nullChar:
                t.error(this);
                t.tagPending.appendAttributeName(replacementChar);
                t.transition(AttributeName);
                break;
            case eof:
                t.eofError(this);
                t.transition(Data);
                break;
            case '"':
            case '\'':
            case '<':
                t.error(this);
                t.tagPending.newAttribute();
                t.tagPending.appendAttributeName(c);
                t.transition(AttributeName);
                break;
            default: // A-Z, anything else
                t.tagPending.newAttribute();
                r.unconsume();
                t.transition(AttributeName);
            }
        }
    },
    BeforeAttributeValue {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                // ignore
                break;
            case '"':
                t.transition(AttributeValue_doubleQuoted);
                break;
            case '&':
                r.unconsume();
                t.transition(AttributeValue_unquoted);
                break;
            case '\'':
                t.transition(AttributeValue_singleQuoted);
                break;
            case nullChar:
                t.error(this);
                t.tagPending.appendAttributeValue(replacementChar);
                t.transition(AttributeValue_unquoted);
                break;
            case eof:
                t.eofError(this);
                t.transition(Data);
                break;
            case '>':
                t.error(this);
                t.emitTagPending();
                t.transition(Data);
                break;
            case '<':
            case '=':
            case '`':
                t.error(this);
                t.tagPending.appendAttributeValue(c);
                t.transition(AttributeValue_unquoted);
                break;
            default:
                r.unconsume();
                t.transition(AttributeValue_unquoted);
            }
        }
    },
    AttributeValue_doubleQuoted {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeToAny('"', '&', nullChar);
            if (value.length() > 0) {
                t.tagPending.appendAttributeValue(value);
            }

            char c = r.consume();
            switch (c) {
            case '"':
                t.transition(AfterAttributeValue_quoted);
                break;
            case '&':
                Character ref = t.consumeCharacterReference('"', true);
                if (ref != null) {
                    t.tagPending.appendAttributeValue(ref);
                } else {
                    t.tagPending.appendAttributeValue('&');
                }
                break;
            case nullChar:
                t.error(this);
                t.tagPending.appendAttributeValue(replacementChar);
                break;
            case eof:
                t.eofError(this);
                t.transition(Data);
                break;
            // no default, handled in consume to any above
            }
        }
    },
    AttributeValue_singleQuoted {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeToAny('\'', '&', nullChar);
            if (value.length() > 0) {
                t.tagPending.appendAttributeValue(value);
            }

            char c = r.consume();
            switch (c) {
            case '\'':
                t.transition(AfterAttributeValue_quoted);
                break;
            case '&':
                Character ref = t.consumeCharacterReference('\'', true);
                if (ref != null) {
                    t.tagPending.appendAttributeValue(ref);
                } else {
                    t.tagPending.appendAttributeValue('&');
                }
                break;
            case nullChar:
                t.error(this);
                t.tagPending.appendAttributeValue(replacementChar);
                break;
            case eof:
                t.eofError(this);
                t.transition(Data);
                break;
            // no default, handled in consume to any above
            }
        }
    },
    AttributeValue_unquoted {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeToAny('\t', '\n', '\f', ' ', '&', '>',
                    nullChar, '"', '\'', '<', '=', '`');
            if (value.length() > 0) {
                t.tagPending.appendAttributeValue(value);
            }

            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                t.transition(BeforeAttributeName);
                break;
            case '&':
                Character ref = t.consumeCharacterReference('>', true);
                if (ref != null) {
                    t.tagPending.appendAttributeValue(ref);
                } else {
                    t.tagPending.appendAttributeValue('&');
                }
                break;
            case '>':
                t.emitTagPending();
                t.transition(Data);
                break;
            case nullChar:
                t.error(this);
                t.tagPending.appendAttributeValue(replacementChar);
                break;
            case eof:
                t.eofError(this);
                t.transition(Data);
                break;
            case '"':
            case '\'':
            case '<':
            case '=':
            case '`':
                t.error(this);
                t.tagPending.appendAttributeValue(c);
                break;
            // no default, handled in consume to any above
            }

        }
    },
    // CharacterReferenceInAttributeValue state handled inline
    AfterAttributeValue_quoted {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                t.transition(BeforeAttributeName);
                break;
            case '/':
                t.transition(SelfClosingStartTag);
                break;
            case '>':
                t.emitTagPending();
                t.transition(Data);
                break;
            case eof:
                t.eofError(this);
                t.transition(Data);
                break;
            default:
                t.error(this);
                r.unconsume();
                t.transition(BeforeAttributeName);
            }

        }
    },
    SelfClosingStartTag {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '>':
                t.tagPending.selfClosing = true;
                t.emitTagPending();
                t.transition(Data);
                break;
            case eof:
                t.eofError(this);
                t.transition(Data);
                break;
            default:
                t.error(this);
                t.transition(BeforeAttributeName);
            }
        }
    },
    BogusComment {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            // todo: handle bogus comment starting from eof. when does that
            // trigger?
            // rewind to capture character that lead us here
            r.unconsume();
            Token.Comment comment = new Token.Comment();
            comment.data.append(r.consumeTo('>'));
            // todo: replace nullChar with replaceChar
            t.emit(comment);
            t.advanceTransition(Data);
        }
    },
    MarkupDeclarationOpen {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchConsume("--")) {
                t.createCommentPending();
                t.transition(CommentStart);
            } else if (r.matchConsumeIgnoreCase("DOCTYPE")) {
                t.transition(Doctype);
            } else if (r.matchConsume("[CDATA[")) {
                // todo: should actually check current namepspace, and only
                // non-html allows cdata. until namespace
                // is implemented properly, keep handling as cdata
                // } else if (!t.currentNodeInHtmlNS() &&
                // r.matchConsume("[CDATA[")) {
                t.transition(CdataSection);
            } else {
                t.error(this);
                t.advanceTransition(BogusComment); // advance so this character
                                                   // gets in bogus comment
                                                   // data's rewind
            }
        }
    },
    CommentStart {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '-':
                t.transition(CommentStartDash);
                break;
            case nullChar:
                t.error(this);
                t.commentPending.data.append(replacementChar);
                t.transition(Comment);
                break;
            case '>':
                t.error(this);
                t.emitCommentPending();
                t.transition(Data);
                break;
            case eof:
                t.eofError(this);
                t.emitCommentPending();
                t.transition(Data);
                break;
            default:
                t.commentPending.data.append(c);
                t.transition(Comment);
            }
        }
    },
    CommentStartDash {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '-':
                t.transition(CommentStartDash);
                break;
            case nullChar:
                t.error(this);
                t.commentPending.data.append(replacementChar);
                t.transition(Comment);
                break;
            case '>':
                t.error(this);
                t.emitCommentPending();
                t.transition(Data);
                break;
            case eof:
                t.eofError(this);
                t.emitCommentPending();
                t.transition(Data);
                break;
            default:
                t.commentPending.data.append(c);
                t.transition(Comment);
            }
        }
    },
    Comment {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.current();
            switch (c) {
            case '-':
                t.advanceTransition(CommentEndDash);
                break;
            case nullChar:
                t.error(this);
                r.advance();
                t.commentPending.data.append(replacementChar);
                break;
            case eof:
                t.eofError(this);
                t.emitCommentPending();
                t.transition(Data);
                break;
            default:
                t.commentPending.data.append(r.consumeToAny('-', nullChar));
            }
        }
    },
    CommentEndDash {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '-':
                t.transition(CommentEnd);
                break;
            case nullChar:
                t.error(this);
                t.commentPending.data.append('-').append(replacementChar);
                t.transition(Comment);
                break;
            case eof:
                t.eofError(this);
                t.emitCommentPending();
                t.transition(Data);
                break;
            default:
                t.commentPending.data.append('-').append(c);
                t.transition(Comment);
            }
        }
    },
    CommentEnd {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '>':
                t.emitCommentPending();
                t.transition(Data);
                break;
            case nullChar:
                t.error(this);
                t.commentPending.data.append("--").append(replacementChar);
                t.transition(Comment);
                break;
            case '!':
                t.error(this);
                t.transition(CommentEndBang);
                break;
            case '-':
                t.error(this);
                t.commentPending.data.append('-');
                break;
            case eof:
                t.eofError(this);
                t.emitCommentPending();
                t.transition(Data);
                break;
            default:
                t.error(this);
                t.commentPending.data.append("--").append(c);
                t.transition(Comment);
            }
        }
    },
    CommentEndBang {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '-':
                t.commentPending.data.append("--!");
                t.transition(CommentEndDash);
                break;
            case '>':
                t.emitCommentPending();
                t.transition(Data);
                break;
            case nullChar:
                t.error(this);
                t.commentPending.data.append("--!").append(replacementChar);
                t.transition(Comment);
                break;
            case eof:
                t.eofError(this);
                t.emitCommentPending();
                t.transition(Data);
                break;
            default:
                t.commentPending.data.append("--!").append(c);
                t.transition(Comment);
            }
        }
    },
    Doctype {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                t.transition(BeforeDoctypeName);
                break;
            case eof:
                t.eofError(this);
                t.createDoctypePending();
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.error(this);
                t.transition(BeforeDoctypeName);
            }
        }
    },
    BeforeDoctypeName {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createDoctypePending();
                t.transition(DoctypeName);
                return;
            }
            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                break; // ignore whitespace
            case nullChar:
                t.error(this);
                t.doctypePending.name.append(replacementChar);
                t.transition(DoctypeName);
                break;
            case eof:
                t.eofError(this);
                t.createDoctypePending();
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.createDoctypePending();
                t.doctypePending.name.append(c);
                t.transition(DoctypeName);
            }
        }
    },
    DoctypeName {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                String name = r.consumeLetterSequence();
                t.doctypePending.name.append(name.toLowerCase());
                return;
            }
            char c = r.consume();
            switch (c) {
            case '>':
                t.emitDoctypePending();
                t.transition(Data);
                break;
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                t.transition(AfterDoctypeName);
                break;
            case nullChar:
                t.error(this);
                t.doctypePending.name.append(replacementChar);
                break;
            case eof:
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.doctypePending.name.append(c);
            }
        }
    },
    AfterDoctypeName {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                return;
            }
            if (r.matchesAny('\t', '\n', '\f', ' ')) {
                r.advance(); // ignore whitespace
            } else if (r.matches('>')) {
                t.emitDoctypePending();
                t.advanceTransition(Data);
            } else if (r.matchConsumeIgnoreCase("PUBLIC")) {
                t.transition(AfterDoctypePublicKeyword);
            } else if (r.matchConsumeIgnoreCase("SYSTEM")) {
                t.transition(AfterDoctypeSystemKeyword);
            } else {
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.advanceTransition(BogusDoctype);
            }

        }
    },
    AfterDoctypePublicKeyword {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                t.transition(BeforeDoctypePublicIdentifier);
                break;
            case '"':
                t.error(this);
                // set public id to empty string
                t.transition(DoctypePublicIdentifier_doubleQuoted);
                break;
            case '\'':
                t.error(this);
                // set public id to empty string
                t.transition(DoctypePublicIdentifier_singleQuoted);
                break;
            case '>':
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            case eof:
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.transition(BogusDoctype);
            }
        }
    },
    BeforeDoctypePublicIdentifier {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                break;
            case '"':
                // set public id to empty string
                t.transition(DoctypePublicIdentifier_doubleQuoted);
                break;
            case '\'':
                // set public id to empty string
                t.transition(DoctypePublicIdentifier_singleQuoted);
                break;
            case '>':
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            case eof:
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.transition(BogusDoctype);
            }
        }
    },
    DoctypePublicIdentifier_doubleQuoted {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '"':
                t.transition(AfterDoctypePublicIdentifier);
                break;
            case nullChar:
                t.error(this);
                t.doctypePending.publicIdentifier.append(replacementChar);
                break;
            case '>':
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            case eof:
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.doctypePending.publicIdentifier.append(c);
            }
        }
    },
    DoctypePublicIdentifier_singleQuoted {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\'':
                t.transition(AfterDoctypePublicIdentifier);
                break;
            case nullChar:
                t.error(this);
                t.doctypePending.publicIdentifier.append(replacementChar);
                break;
            case '>':
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            case eof:
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.doctypePending.publicIdentifier.append(c);
            }
        }
    },
    AfterDoctypePublicIdentifier {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                t.transition(BetweenDoctypePublicAndSystemIdentifiers);
                break;
            case '>':
                t.emitDoctypePending();
                t.transition(Data);
                break;
            case '"':
                t.error(this);
                // system id empty
                t.transition(DoctypeSystemIdentifier_doubleQuoted);
                break;
            case '\'':
                t.error(this);
                // system id empty
                t.transition(DoctypeSystemIdentifier_singleQuoted);
                break;
            case eof:
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.transition(BogusDoctype);
            }
        }
    },
    BetweenDoctypePublicAndSystemIdentifiers {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                break;
            case '>':
                t.emitDoctypePending();
                t.transition(Data);
                break;
            case '"':
                t.error(this);
                // system id empty
                t.transition(DoctypeSystemIdentifier_doubleQuoted);
                break;
            case '\'':
                t.error(this);
                // system id empty
                t.transition(DoctypeSystemIdentifier_singleQuoted);
                break;
            case eof:
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.transition(BogusDoctype);
            }
        }
    },
    AfterDoctypeSystemKeyword {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                t.transition(BeforeDoctypeSystemIdentifier);
                break;
            case '>':
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            case '"':
                t.error(this);
                // system id empty
                t.transition(DoctypeSystemIdentifier_doubleQuoted);
                break;
            case '\'':
                t.error(this);
                // system id empty
                t.transition(DoctypeSystemIdentifier_singleQuoted);
                break;
            case eof:
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
            }
        }
    },
    BeforeDoctypeSystemIdentifier {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                break;
            case '"':
                // set system id to empty string
                t.transition(DoctypeSystemIdentifier_doubleQuoted);
                break;
            case '\'':
                // set public id to empty string
                t.transition(DoctypeSystemIdentifier_singleQuoted);
                break;
            case '>':
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            case eof:
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.transition(BogusDoctype);
            }
        }
    },
    DoctypeSystemIdentifier_doubleQuoted {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '"':
                t.transition(AfterDoctypeSystemIdentifier);
                break;
            case nullChar:
                t.error(this);
                t.doctypePending.systemIdentifier.append(replacementChar);
                break;
            case '>':
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            case eof:
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.doctypePending.systemIdentifier.append(c);
            }
        }
    },
    DoctypeSystemIdentifier_singleQuoted {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\'':
                t.transition(AfterDoctypeSystemIdentifier);
                break;
            case nullChar:
                t.error(this);
                t.doctypePending.systemIdentifier.append(replacementChar);
                break;
            case '>':
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            case eof:
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.doctypePending.systemIdentifier.append(c);
            }
        }
    },
    AfterDoctypeSystemIdentifier {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case ' ':
                break;
            case '>':
                t.emitDoctypePending();
                t.transition(Data);
                break;
            case eof:
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                t.error(this);
                t.transition(BogusDoctype);
                // NOT force quirks
            }
        }
    },
    BogusDoctype {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
            case '>':
                t.emitDoctypePending();
                t.transition(Data);
                break;
            case eof:
                t.emitDoctypePending();
                t.transition(Data);
                break;
            default:
                // ignore char
                break;
            }
        }
    },
    CdataSection {
        @Override
        void read(Tokeniser t, CharacterReader r) {
            String data = r.consumeTo("]]>");
            t.emit(data);
            r.matchConsume("]]>");
            t.transition(Data);
        }
    };

    abstract void read(Tokeniser t, CharacterReader r);

    private static final char nullChar = '\u0000';
    private static final char replacementChar = Tokeniser.replacementChar;
    private static final String replacementStr = String
            .valueOf(Tokeniser.replacementChar);
    private static final char eof = CharacterReader.EOF;
}
