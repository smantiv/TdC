import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // Leer el código fuente desde el archivo
        String code = Files.readString(Path.of("programaEjemplo.se"));
        
        // Crear el lexer y parser
        SELexer lexer = new SELexer(CharStreams.fromString(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SEParser parser = new SEParser(tokens);
        
        // Construir el árbol sintáctico
        ParseTree tree = parser.programa();


        // Ejecutar el intérprete
        SEInterpreter interpreter = new SEInterpreter();
        interpreter.visit(tree);
    }
}

