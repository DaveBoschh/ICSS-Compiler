package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANVariableTable;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;

public class Evaluator implements Transform {

    //    private IHANLinkedList<HashMap<String, Literal>> variableValues;
    private HANVariableTable<String, Literal> variableValues;

    public Evaluator() {
        variableValues = new HANVariableTable<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANVariableTable<>();
        variableValues.pushScope();
        loopChildren(ast.root);

    }


    private void loopChildren(ASTNode node) {
        variableValues.checkPushScope(node);
        for (ASTNode child : node.getChildren()) {
            evaluateOperation(child, node);
            evaluateVariableAssignment(child, node);
            evaluateVariableReference(child, node);
            evaluateIfClause(child, node);
            loopChildren(child);
        }
        variableValues.checkPopScope(node);
    }

    private void evaluateIfClause(ASTNode child, ASTNode parent) {
        if (!(child instanceof IfClause)) {
            return;
        }
        IfClause ifClause = (IfClause) child;
        Boolean condition = null;
        if (ifClause.conditionalExpression instanceof VariableReference) {
            condition = ((BoolLiteral) getExpressionValue(ifClause.conditionalExpression)).value;
        } else if (ifClause.conditionalExpression instanceof BoolLiteral) {
            condition = ((BoolLiteral) ifClause.conditionalExpression).value;
        }
        if (condition) {
//            System.out.println("Parent: " + parent.getNodeLabel());
//            System.out.println("Child: " + child.getNodeLabel());
            parent.removeChild(ifClause);
            for (ASTNode node : ifClause.body) {
                if (node instanceof IfClause) {
                    evaluateIfClause(node, parent);
                } else {
                    parent.addChild(node);
                }
            }
        } else {
            parent.removeChild(ifClause);
            if (ifClause.elseClause != null) {
                for (ASTNode node : ifClause.elseClause.body) {
                    if (node instanceof IfClause) {
                        evaluateIfClause(node, parent);
                    } else {
                        parent.addChild(node);
                    }
                }
            }

        }
    }

    private void evaluateOperation(ASTNode child, ASTNode parent) {
        if (!(child instanceof Operation)) {
            return;
        }
        Operation operation = (Operation) child;

        if (operation.rhs instanceof Operation) {
            evaluateOperation(operation.rhs, operation);
        } else if (operation.lhs instanceof Operation) {
            evaluateOperation(operation.lhs, operation);
        }

        replaceNode(parent, child, calculateOperation(operation));
    }

    private Literal calculateOperation(Operation operation) {
        Literal lhs = (operation.lhs instanceof Operation) ? calculateOperation((Operation) operation.lhs) : getExpressionValue(operation.lhs);
        Literal rhs = (operation.rhs instanceof Operation) ? calculateOperation((Operation) operation.rhs) : getExpressionValue(operation.rhs);

        int leftValue;
        int rightValue;

        if (lhs instanceof ScalarLiteral) {
            leftValue = ((ScalarLiteral) lhs).value;
        } else if (lhs instanceof PixelLiteral) {
            leftValue = ((PixelLiteral) lhs).value;
        } else if (lhs instanceof PercentageLiteral) {
            leftValue = ((PercentageLiteral) lhs).value;
        } else {
            return null;
        }

        if (rhs instanceof ScalarLiteral) {
            rightValue = ((ScalarLiteral) rhs).value;
        } else if (rhs instanceof PixelLiteral) {
            rightValue = ((PixelLiteral) rhs).value;
        } else if (rhs instanceof PercentageLiteral) {
            rightValue = ((PercentageLiteral) rhs).value;
        } else {
            return null;
        }

        int calculatedValue;

        if (operation instanceof MultiplyOperation) {
            calculatedValue = leftValue * rightValue;
        } else if (operation instanceof AddOperation) {
            calculatedValue = leftValue + rightValue;
        } else { // Subtractoperation
            calculatedValue = leftValue - rightValue;
        }


        Literal type = (lhs instanceof ScalarLiteral) ? rhs : lhs;

        return createNewLiteral(type, calculatedValue);
    }

    private Literal createNewLiteral(Literal type, int value) {
        if (type instanceof PercentageLiteral) {
            return new PercentageLiteral(value);
        } else if (type instanceof ScalarLiteral) {
            return new ScalarLiteral(value);
        } else {
            return new PixelLiteral(value);
        }
    }


    private void evaluateVariableAssignment(ASTNode child, ASTNode parent) {
        if (!(child instanceof VariableAssignment)) return;
        VariableAssignment variableAssignment = (VariableAssignment) child;

        Literal value;
        if (variableAssignment.expression instanceof Operation) {
            variableValues.insertVariable(variableAssignment.name.name, calculateOperation((Operation) variableAssignment.expression));
        } else
            variableValues.insertVariable(variableAssignment.name.name, getExpressionValue(variableAssignment.expression));

    }

    private boolean isAssignmentOperation(VariableAssignment variableAssignment) {
        for (ASTNode child : variableAssignment.getChildren()) {
            if (child instanceof Operation) {
                return true;
            }
        }
        return false;
    }

    private void evaluateVariableReference(ASTNode child, ASTNode parent) {
        if (!(child instanceof VariableReference) || (parent instanceof VariableAssignment)) return;
        VariableReference variableReference = (VariableReference) child;
        replaceNode(parent, child, getExpressionValue(variableReference));
    }

    private Literal getExpressionValue(Expression expression) {
        if (expression instanceof Literal) {
            return (Literal) expression;
        }

        if (expression instanceof VariableReference) {
            VariableReference variableReference = (VariableReference) expression;
            return variableValues.getVariable(variableReference.name);
        }
        return null;
    }


    public void replaceNode(ASTNode parent, ASTNode oldChild, ASTNode newChild) {
        parent.removeChild(oldChild);
        parent.addChild(newChild);
    }

}

