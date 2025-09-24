package io.jeyong.resilience4j.navigator;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.Nullable;

public class CircuitBreakerGotoHandler implements GotoDeclarationHandler {

    private final AnnotationToMethodResolver annotationResolver = new AnnotationToMethodResolver();
    private final MethodToAnnotationResolver methodResolver = new MethodToAnnotationResolver();

    @Override
    public @Nullable PsiElement[] getGotoDeclarationTargets(PsiElement sourceElement, int offset, Editor editor) {
        if (sourceElement == null || !sourceElement.isValid()) {
            return null;
        }

        if (sourceElement instanceof PsiJavaToken token && token.getParent() instanceof PsiLiteralExpression literal) {
            sourceElement = literal;
        }

        if (sourceElement instanceof PsiLiteralExpression literal) {
            return annotationResolver.resolveFromLiteral(literal);
        }

        if (sourceElement instanceof PsiIdentifier id && id.getParent() instanceof com.intellij.psi.PsiMethod) {
            return methodResolver.resolveFromMethodIdentifier(id);
        }

        return null;
    }
}
