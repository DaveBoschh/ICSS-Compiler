package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

public class Generator {
    StringBuilder stringBuilder;

    public String generate(AST ast) {
        stringBuilder = new StringBuilder();
        for (ASTNode child : ast.root.getChildren()) {
            generateStyleRule(child);

        }

        return stringBuilder.toString();


    }

    private void generateStyleRule(ASTNode child) {
        if (!(child instanceof Stylerule)) {
            return;
        }
        Stylerule stylerule = (Stylerule) child;
		 for(Selector selector: stylerule.selectors){
             stringBuilder.append(selector.toString());
             stringBuilder.append(" {\n");
             for (ASTNode styleBody: stylerule.body){
                 generateDeclaration(styleBody);
             }
             stringBuilder.append("} \n");
         }

    }

    private void generateDeclaration(ASTNode styleBody) {
        Declaration declaration = (Declaration) styleBody;
        stringBuilder.append("  ");
        stringBuilder.append(declaration.property.name);
        stringBuilder.append(":");
        generateLiteral(declaration.expression);
        stringBuilder.append(";");
        stringBuilder.append("\n");

    }

    private void generateLiteral(ASTNode literal) {
        if (literal instanceof PixelLiteral){
            stringBuilder.append(((PixelLiteral) literal).value);
            stringBuilder.append("px");
        }else if (literal instanceof PercentageLiteral){
            stringBuilder.append(((PercentageLiteral) literal).value);
            stringBuilder.append("%");
        } else if (literal instanceof ColorLiteral) {
            stringBuilder.append(((ColorLiteral) literal).value);

        }
    }


}
