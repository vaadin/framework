package com.vaadin.checks;

import java.io.Serializable;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class CheckClassesSerializable extends Check {

    private StringBuilder classNameBuilder;

    private String packageName;

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,
                TokenTypes.INTERFACE_DEF };
    }

    @Override
    public void visitToken(DetailAST aAST) {
        if (aAST.getType() == TokenTypes.PACKAGE_DEF) {
            final DetailAST nameAST = aAST.getLastChild().getPreviousSibling();
            final FullIdent full = FullIdent.createFullIdent(nameAST);
            packageName = full.getText();
        } else {
            final String className = packageName + "." + getClassName(aAST);
            try {
                final Class<?> cls =
                // Class.forName(className);
                Class.forName(className, true, getClassLoader());
                if (!cls.isAnnotation() && !cls.isSynthetic()
                        && !Serializable.class.isAssignableFrom(cls)) {
                    log(aAST.getLine(), aAST.getClass()
                            + " is not Serializable");
                }
            } catch (ClassNotFoundException e) {
                log(aAST.getLine(), "Could not load class " + className + " "
                        + aAST.getClass());
                e.printStackTrace();
            } catch (Throwable t) {
                log(aAST.getLine(), "Could not load class " + className + " "
                        + aAST.getClass());
                t.printStackTrace();
            }
        }
    }

    private String getClassName(DetailAST classDefAST) {
        FullIdent ident = FullIdent.createFullIdent(classDefAST
                .findFirstToken(TokenTypes.IDENT));
        return ident.getText();
    }
}
