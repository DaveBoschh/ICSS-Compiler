package nl.han.ica.icss.parser;

import java.util.Stack;


import com.sun.prism.impl.shape.OpenPiscesRasterizer;
import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

    //Accumulator attributes:
    private AST ast;

    //Use this to keep track of the parent nodes when recursively traversing the ast
    private IHANStack<ASTNode> currentContainer;

    public ASTListener() {
        ast = new AST();
        currentContainer = new HANStack<>();
        currentContainer.push(ast.root);
    }

    public AST getAST() {
        return ast;
    }



    @Override
    public void enterStyleRule(ICSSParser.StyleRuleContext ctx) {
        ASTNode styleRule = new Stylerule();
        currentContainer.push(styleRule);
    }

    @Override
    public void exitStyleRule(ICSSParser.StyleRuleContext ctx) {
        ASTNode styleRule = currentContainer.pop();
        currentContainer.peek().addChild(styleRule);
    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        ASTNode declaration = new Declaration();
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        ASTNode declaration = currentContainer.pop();
        currentContainer.peek().addChild(declaration);
    }

    @Override
    public void enterPropertyName(ICSSParser.PropertyNameContext ctx) {
        ASTNode propertyName = new PropertyName(ctx.getText());
        currentContainer.push(propertyName);
    }

    @Override
    public void exitPropertyName(ICSSParser.PropertyNameContext ctx) {
        ASTNode propertyName = currentContainer.pop();
        currentContainer.peek().addChild(propertyName);
    }

    @Override
    public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        ASTNode colorLiteral = new ColorLiteral(ctx.getText());
        currentContainer.peek().addChild(colorLiteral);
    }

    @Override
    public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
        ASTNode boolLiteral = new BoolLiteral(ctx.getText());
        currentContainer.peek().addChild(boolLiteral);
    }

    @Override
    public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
        ASTNode percentageLiteral = new PercentageLiteral(ctx.getText());
        currentContainer.peek().addChild(percentageLiteral);
    }

    @Override
    public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        ASTNode pixelLiteral = new PixelLiteral(ctx.getText());
        currentContainer.peek().addChild(pixelLiteral);
    }

    @Override
    public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
        ASTNode scalarLiteral = new ScalarLiteral(ctx.getText());
        currentContainer.peek().addChild(scalarLiteral);
    }

    @Override
    public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
        ASTNode tagSelector = new TagSelector(ctx.getText());
        currentContainer.peek().addChild(tagSelector);
    }

    @Override
    public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
        ASTNode classSelector = new ClassSelector(ctx.getText());
        currentContainer.peek().addChild(classSelector);
    }


    @Override
    public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
        ASTNode idSelector = new IdSelector(ctx.getText());
        currentContainer.peek().addChild(idSelector);
    }

    @Override
    public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        ASTNode variableAssignment = new VariableAssignment();
        currentContainer.push(variableAssignment);
    }

    @Override
    public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        ASTNode variableAssignment = currentContainer.pop();
        currentContainer.peek().addChild(variableAssignment);
    }

    @Override
    public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
        ASTNode variableReference = new VariableReference(ctx.getText());
        currentContainer.push(variableReference);
    }

    @Override
    public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
        ASTNode variableReference = currentContainer.pop();
        currentContainer.peek().addChild(variableReference);
    }

    @Override
    public void enterExpression(ICSSParser.ExpressionContext ctx) {
        if (ctx.getChildCount() == 3) {
            Operation operation = null;
            if (ctx.getChild(1).getText().equals("*")) {
                operation = new MultiplyOperation();
            } else if (ctx.getChild(1).getText().equals("+")) {
                operation = new AddOperation();
            } else if (ctx.getChild(1).getText().equals("-")) {
                operation = new SubtractOperation();
            }
            currentContainer.push(operation);
        }
    }

    @Override
    public void exitExpression(ICSSParser.ExpressionContext ctx) {
        if (ctx.PLUS() != null || ctx.MIN() != null || ctx.MUL() != null) {
            ASTNode operation = currentContainer.pop();
            currentContainer.peek().addChild(operation);
        }
    }

    @Override
    public void enterIfClause(ICSSParser.IfClauseContext ctx) {
        ASTNode ifClause = new IfClause();
        currentContainer.push(ifClause);
    }

    @Override
    public void exitIfClause(ICSSParser.IfClauseContext ctx) {
        ASTNode ifClause = currentContainer.pop();
        currentContainer.peek().addChild(ifClause);
    }

    @Override
    public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
        ASTNode elseClause = new ElseClause();
        currentContainer.push(elseClause);
    }

    @Override
    public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
        ASTNode elseClause = currentContainer.pop();
        currentContainer.peek().addChild(elseClause);
    }


}