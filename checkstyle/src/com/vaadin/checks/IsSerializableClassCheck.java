package com.vaadin.checks;

import java.io.Serializable;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.AbstractTypeAwareCheck;

public class IsSerializableClassCheck extends AbstractTypeAwareCheck {

    private String currentClassName;

    @Override
    protected void logLoadError(Token arg0) {
        log(arg0.getLineNo(), arg0.getColumnNo(),
                "Could not load "
                        + arg0.getText()
                        + " on "
                        + (currentClassName != null ? currentClassName
                                : "null className"));
    }

    @Override
    protected void processAST(DetailAST ast) {
        currentClassName = getCurrentClassName();
        if (currentClassName == null || currentClassName.isEmpty()) {
            currentClassName = ast.findFirstToken(TokenTypes.IDENT).getText();
        }
        if (currentClassName == null || currentClassName.isEmpty()) {
            log(ast.getLine(),
                    "Could not get classname of type " + ast.getType() + " "
                            + ast.getText());
        } else {
            Class<?> cls = tryLoadClass(
                    new Token(FullIdent.createFullIdent(ast)), currentClassName);
            if (!cls.isAnnotation() && !cls.isSynthetic()
                    && !Serializable.class.isAssignableFrom(cls)) {
                log(ast.getLine(), currentClassName + " is not Serializable");
            }
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF };
    }

}
