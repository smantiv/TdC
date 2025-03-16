import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SEInterpreter extends SEBaseVisitor<Integer> {
    private final Map<String, Integer> memoria = new HashMap<>(); // Almacena variables
    private final Scanner scanner = new Scanner(System.in); // Para leer entrada del usuario

    @Override
    public Integer visitDeclaracionGlobal(SEParser.DeclaracionGlobalContext ctx) {
        SEParser.ListaDeclaracionesContext lista = ctx.listaDeclaraciones();
        for (int i = 0; i < lista.ID().size(); i++) {
            String varName = lista.ID(i).getText();
            Integer value = (lista.expr(i) != null) ? visit(lista.expr(i)) : 0;
            memoria.put(varName, value);
            System.out.println("Variable declarada: " + varName + " = " + value); // Debug
        }
        return 0;
    }

    @Override
    public Integer visitAsignacion(SEParser.AsignacionContext ctx) {
        String varName = ctx.ID().getText();
        int value = visit(ctx.expr());
        memoria.put(varName, value);
        System.out.println("Asignación: " + varName + " = " + value); // Debug
        return value;
    }

    @Override
    public Integer visitLectura(SEParser.LecturaContext ctx) {
        String varName = ctx.ID().getText();
        System.out.print("Ingrese valor para " + varName + ": ");
        int value = scanner.nextInt();
        memoria.put(varName, value);
        return value;
    }

    @Override
    public Integer visitEscritura(SEParser.EscrituraContext ctx) {
        for (SEParser.ExprContext expr : ctx.expr()) {
            Integer resultado = visit(expr);
            System.out.println(resultado);
        }
        return 0;
    }

    @Override
    public Integer visitCondicionSi(SEParser.CondicionSiContext ctx) {
        boolean condicion = visit(ctx.expr()) != 0;
        if (condicion) {
            return visit(ctx.bloque(0));
        } else if (ctx.bloque(1) != null) {
            return visit(ctx.bloque(1));
        }
        return 0;
    }

    @Override
    public Integer visitCicloMientras(SEParser.CicloMientrasContext ctx) {
        while (visit(ctx.expr()) != 0) {
            System.out.println("Ejecutando ciclo mientras...");
            visit(ctx.bloque());
        }
        return 0;
    }

    @Override
    public Integer visitCicloHacer(SEParser.CicloHacerContext ctx) {
        do {
            visit(ctx.bloque());
        } while (visit(ctx.expr()) != 0);
        return 0;
    }

    @Override
    public Integer visitExprRelacional(SEParser.ExprRelacionalContext ctx) {
        // Si solo hay un hijo, es el caso base (exprAritmetica)
        if (ctx.getChildCount() == 1) {
            return visit(ctx.getChild(0));
        } else {
            // Estructura: exprRelacional op exprAritmetica
            int left = visit(ctx.getChild(0));
            int right = visit(ctx.getChild(2));
            String op = ctx.getChild(1).getText();
            switch (op) {
                case "==": return (left == right) ? 1 : 0;
                case "!=": return (left != right) ? 1 : 0;
                case "<": return (left < right) ? 1 : 0;
                case "<=": return (left <= right) ? 1 : 0;
                case ">": return (left > right) ? 1 : 0;
                case ">=": return (left >= right) ? 1 : 0;
                default: throw new RuntimeException("Operador relacional desconocido: " + op);
            }
        }
    }

    @Override
    public Integer visitExprAritmetica(SEParser.ExprAritmeticaContext ctx) {
        if (ctx.exprAritmetica() != null) {
            int left = visit(ctx.exprAritmetica());
            int right = visit(ctx.exprPotencia());
            String op = ctx.getChild(1).getText();
            switch (op) {
                case "+": return left + right;
                case "-": return left - right;
                default: throw new RuntimeException("Operador desconocido: " + op);
            }
        }
        return visit(ctx.exprPotencia());
    }

    @Override
    public Integer visitExprPotencia(SEParser.ExprPotenciaContext ctx) {
        if (ctx.exprPotencia() != null) {
            int base = visit(ctx.exprPotencia());
            int exp = visit(ctx.exprTermino());
            return (int) Math.pow(base, exp);
        }
        return visit(ctx.exprTermino());
    }

    @Override
    public Integer visitExprTermino(SEParser.ExprTerminoContext ctx) {
        if (ctx.exprTermino() != null) {
            int left = visit(ctx.exprTermino());
            int right = visit(ctx.exprFactor());
            String op = ctx.getChild(1).getText();
            switch (op) {
                case "*": return left * right;
                case "/": return right != 0 ? left / right : 0;
                case "%": return left % right;
                default: throw new RuntimeException("Operador desconocido: " + op);
            }
        }
        return visit(ctx.exprFactor());
    }

    @Override
    public Integer visitExprFactor(SEParser.ExprFactorContext ctx) {
        if (ctx.NUMERO() != null) {
            return Integer.parseInt(ctx.NUMERO().getText());
        } else if (ctx.CADENA() != null) {
            // Imprime la cadena sin las comillas
            System.out.println(ctx.CADENA().getText().replace("\"", ""));
            return 0;
        } else if (ctx.PARI() != null) {
            return visit(ctx.expr());
        } else if (ctx.ID() != null) {
            String varName = ctx.ID().getText();
            return memoria.getOrDefault(varName, 0);
        } else {
            throw new RuntimeException("Expresión inválida en visitExprFactor.");
        }
    }
}




