package nl.han.ica.datastructures;

import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.IfClause;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class HANVariableTable<T,U> {
    private IHANLinkedList<HashMap<T, U>> variableTypes;

    public HANVariableTable() {
        variableTypes = new HANLinkedList<>();
    }

    public void pushScope() {
        variableTypes.addFirst(new HashMap<>());
    }

    public void popScope() {
        variableTypes.removeFirst();
    }

    public void insertVariable(T name, U expressionType) {
        variableTypes.getFirst().put(name, expressionType);
    }

    public U getVariable(T name) {
        if (variableTypes.getFirst() == null) {
            return null;
        }
        for (int i = 0; i < variableTypes.getSize(); i++) {
            if (variableTypes.get(i).get(name) != null) {
                return variableTypes.get(i).get(name);
            }
        }
        return null;
    }

    public void checkPopScope(ASTNode node) {
        if ((node instanceof Stylerule) | (node instanceof IfClause)) {
            popScope();
        }
    }

    public void checkPushScope(ASTNode node) {
        if ((node instanceof Stylerule) | (node instanceof IfClause)) {
            pushScope();
        }

    }
}
