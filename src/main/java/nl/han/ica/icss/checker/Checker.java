package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.HANVariableTable;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;


public class Checker {

    private HANVariableTable<String, ExpressionType> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANVariableTable();
        variableTypes.pushScope();

        loopChildren(ast.root);
    }

    public void loopChildren(ASTNode node) {
        variableTypes.checkPushScope(node);
        for (ASTNode child : node.getChildren()) {
            checkDeclaration(child);
            checkOperation(child);
            checkIfStatement(child);
            addToScope(child);
            loopChildren(child);
        }
        variableTypes.checkPopScope(node);

    }

    public void addToScope(ASTNode node) {
        if (node instanceof VariableAssignment) {
            VariableAssignment variableAssignment = (VariableAssignment) node;
            if (isAssignmentOperation(variableAssignment)) {
                variableTypes.insertVariable(variableAssignment.name.name, getOperationType((Operation) variableAssignment.expression));
                return;
            }

            variableTypes.insertVariable(variableAssignment.name.name, getExpressionType(variableAssignment.expression));
        }
    }

    private ExpressionType getOperationType(Operation operation) {
        if (operation.rhs instanceof Operation)
            return getOperationType((Operation) operation.rhs);
        if (operation.lhs instanceof Operation)
            return getOperationType((Operation) operation.lhs);
        if (operation.lhs instanceof ScalarLiteral) {
            return getExpressionType(operation.rhs);
        }else { //rhs is scalar
            return getExpressionType(operation.lhs);
        }

    }


    private boolean isAssignmentOperation(VariableAssignment variableAssignment) {
        for (ASTNode child : variableAssignment.getChildren()) {
            if (child instanceof Operation) {
                return true;
            }
        }
        return false;
    }


    public ExpressionType getExpressionType(Expression expression) {
        if (expression instanceof ColorLiteral)
            return ExpressionType.COLOR;
        if (expression instanceof PixelLiteral)
            return ExpressionType.PIXEL;
        if (expression instanceof PercentageLiteral)
            return ExpressionType.PERCENTAGE;
        if (expression instanceof ScalarLiteral)
            return ExpressionType.SCALAR;
        if (expression instanceof BoolLiteral)
            return ExpressionType.BOOL;
        if (expression instanceof VariableReference) {
            VariableReference variableReference = (VariableReference) expression;
            return variableTypes.getVariable(variableReference.name);
        }
        return null;
    }


    public void checkDeclaration(ASTNode node) {
        if (node instanceof Declaration) {
            Declaration declaration = (Declaration) node;
            if (getExpressionType(declaration.expression) == null && !(declaration.expression instanceof Operation))  {
                declaration.expression.setError("Variable is not declared in scope!");
            } else if (declaration.property.name.contains("color") && getExpressionType(declaration.expression) != ExpressionType.COLOR) {
                declaration.expression.setError("Must be color literal!");
//            } else if ((declaration.property.name.equals("width") | declaration.property.name.equals("height")) && !(getExpressionType(declaration.expression) == ExpressionType.PIXEL || getExpressionType(declaration.expression) == ExpressionType.PERCENTAGE || declaration.expression instanceof Operation)) {
            } else if (declaration.property.name.equals("width") | declaration.property.name.equals("height"))
                if (!(getExpressionType(declaration.expression) == ExpressionType.PIXEL || getExpressionType(declaration.expression) == ExpressionType.PERCENTAGE)) {
                    if (declaration.expression instanceof Operation){
                        return;
                    }
//                    var test = getExpressionType(declaration.expression);
                    declaration.expression.setError("Must be pixel literal or percentage literal!");
                }
            }
        }


    //
    public void checkOperation(ASTNode node) {
        if (!(node instanceof Operation)) {
            return;
        }
            Operation operation = (Operation) node;
            if (getExpressionType(operation.lhs) == ExpressionType.COLOR | getExpressionType(operation.rhs) == ExpressionType.COLOR) {
                operation.setError("Cant use a color in operation!");
            }

    }

    public void checkIfStatement(ASTNode node) {
        if (node instanceof IfClause) {
            IfClause ifClause = (IfClause) node;
            if (getExpressionType(ifClause.conditionalExpression) != ExpressionType.BOOL)
                ifClause.conditionalExpression.setError("Conditional expression must be a boolean!");
        }
    }

}

    

