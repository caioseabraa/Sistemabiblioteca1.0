import java.util.ArrayList;
import java.util.List;

// ============================================================
// QUESTÃO 1 — Classes e Encapsulamento
// ============================================================

// Classe Autor: representa quem escreveu o livro
class Autor {
    private String nome;
    private String nacionalidade;
    private String dataNascimento;                     // data como texto: "21/06/1839"
    private List<Livro> livros = new ArrayList<>();    // Q2: associação 1:N com Livro

    public Autor(String nome, String nacionalidade, String dataNascimento) {
        this.nome = nome;
        this.nacionalidade = nacionalidade;
        this.dataNascimento = dataNascimento;
    }

    public String getNome() { return nome; }
    public List<Livro> getLivros() { return livros; }

    // Q2.1 — Adiciona livro à lista do autor e define este como autor do livro
    public void adicionarLivro(Livro livro) {
        if (!livros.contains(livro)) {
            livros.add(livro);
            livro.setAutor(this); // navegabilidade bidirecional
        }
    }
}

// ============================================================
// Q3.1 — COMPOSIÇÃO: Capítulo só existe dentro de um Livro
// ============================================================

// Construtor sem "public" = package-private: só o Livro pode criar Capítulos
class Capitulo {
    private int numero;
    private String titulo;
    private int paginas;

    Capitulo(int numero, String titulo, int paginas) {
        this.numero = numero;
        this.titulo = titulo;
        this.paginas = paginas;
    }

    public String toString() {
        return "Cap. " + numero + ": " + titulo + " (" + paginas + " págs.)";
    }
}

// ============================================================
// Classe Livro — associada a Autor (Q2) e composta de Capítulos (Q3)
// ============================================================

class Livro {
    private String titulo;
    private String isbn;
    private int anoPublicacao;
    private int exemplares;
    private Autor autor;                                   // associação N:1 com Autor
    private List<Capitulo> capitulos = new ArrayList<>();  // composição com Capítulo

    public Livro(String titulo, String isbn, int anoPublicacao, int exemplares) {
        this.titulo = titulo;
        this.isbn = isbn;
        this.anoPublicacao = anoPublicacao;
        this.exemplares = exemplares;
    }

    public String getTitulo()              { return titulo; }
    public int getExemplares()             { return exemplares; }
    public Autor getAutor()                { return autor; }
    public List<Capitulo> getCapitulos()   { return capitulos; }

    public void setExemplares(int exemplares) { this.exemplares = exemplares; }

    // Setter sem "public": usado internamente pela navegabilidade bidirecional
    void setAutor(Autor autor) { this.autor = autor; }

    // Q3.1 — Composição: o Livro cria e controla seus Capítulos
    public void adicionarCapitulo(int numero, String titulo, int paginas) {
        capitulos.add(new Capitulo(numero, titulo, paginas));
    }

    public String toString() { return titulo; }
}

// ============================================================
// QUESTÃO 1 — Classe Leitor
// ============================================================

class Leitor {
    private String nome;
    private String cpf;
    private String email;
    private String dataCadastro;                              // data como texto: "15/01/2023"
    private List<Emprestimo> emprestimos = new ArrayList<>(); // Q2.2: 1:N com Emprestimo

    public Leitor(String nome, String cpf, String email, String dataCadastro) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.dataCadastro = dataCadastro;
    }

    public String getNome() { return nome; }
    public List<Emprestimo> getEmprestimos() { return emprestimos; }

    void adicionarEmprestimo(Emprestimo emp) { emprestimos.add(emp); }

    // Q4 — Soma multas de todos os empréstimos; dobra se mais de 3 em atraso
    public double calcularMultaTotal() {
        double subtotal = 0;
        int emAtraso = 0;

        for (Emprestimo emp : emprestimos) {
            double multa = emp.calcularMulta();
            subtotal += multa;
            if (multa > 0) emAtraso++;
        }

        return emAtraso > 3 ? subtotal * 2 : subtotal;
    }
}

// ============================================================
// QUESTÃO 2.2 — Classe Emprestimo
// ============================================================

/*
 * DECISÃO: realizarEmprestimo() está em Emprestimo porque esta classe
 * é a responsável pelo contrato — ela une leitor, livro e dias.
 * Colocar esse método em Leitor ou Livro violaria a responsabilidade única.
 */
class Emprestimo {
    private static final double MULTA_POR_DIA = 1.50;

    private Leitor leitor;       // N:1 com Leitor
    private Livro livro;         // N:1 com Livro
    private int diasEmprestimo;  // prazo original do empréstimo
    private int diasAtraso;      // dias passados da data prevista
    private String status = "ativo";

    // Construtor privado: só pode ser criado via realizarEmprestimo()
    private Emprestimo(Leitor leitor, Livro livro, int diasEmprestimo, int diasAtraso) {
        this.leitor = leitor;
        this.livro = livro;
        this.diasEmprestimo = diasEmprestimo;
        this.diasAtraso = diasAtraso;
    }

    public int getDiasAtraso() { return diasAtraso; }
    public Livro getLivro()    { return livro; }

    // Q2.2 — Fábrica estática: cria o empréstimo, registra no leitor e baixa estoque
    public static Emprestimo realizarEmprestimo(Leitor leitor, Livro livro,
                                                int diasEmprestimo, int diasAtraso) {
        livro.setExemplares(livro.getExemplares() - 1);
        Emprestimo emp = new Emprestimo(leitor, livro, diasEmprestimo, diasAtraso);
        leitor.adicionarEmprestimo(emp);
        return emp;
    }

    // Q4 — Calcula multa: R$ 1,50 por dia de atraso; 0 se não houver atraso
    public double calcularMulta() {
        return diasAtraso > 0 ? diasAtraso * MULTA_POR_DIA : 0;
    }
}

// ============================================================
// QUESTÃO 3.2 — AGREGAÇÃO: Biblioteca agrega Livros
// ============================================================

/*
 * DIFERENÇA: Composição (Livro + Capítulo) → capítulo não existe sem o livro.
 *            Agregação (Biblioteca + Livro) → livro existe independente da biblioteca.
 */
class Biblioteca {
    private String nome;
    private String endereco;
    private List<Livro> acervo = new ArrayList<>(); // agregação: não cria nem destrói livros

    public Biblioteca(String nome, String endereco) {
        this.nome = nome;
        this.endereco = endereco;
    }

    public void adicionarLivro(Livro livro) { acervo.add(livro); }
    public void removerLivro(Livro livro)   { acervo.remove(livro); }

    public String toString() {
        return nome + " — " + acervo.size() + " livros no acervo";
    }
}

// ============================================================
// QUESTÃO 4 — Main: simulação completa
// ============================================================

public class SistemaBibliotecaFinal {

    public static void main(String[] args) {

        // Criar autores
        Autor machado = new Autor("Machado de Assis", "Brasileira", "21/06/1839");
        Autor aluisio = new Autor("Aluísio Azevedo",  "Brasileira", "14/04/1857");
        Autor jose    = new Autor("José de Alencar",  "Brasileira", "01/05/1829");
        Autor jorge   = new Autor("Jorge Amado",      "Brasileira", "10/08/1912");

        // Criar livros
        Livro domCasmurro = new Livro("Dom Casmurro",      "978-85-359-0277-5", 1899, 5);
        Livro oCortico    = new Livro("O Cortiço",         "978-85-260-0022-9", 1890, 5);
        Livro iracema     = new Livro("Iracema",           "978-85-259-0100-2", 1865, 5);
        Livro capitaes    = new Livro("Capitães da Areia", "978-85-359-0300-0", 1937, 5);

        // Associar autores ↔ livros (navegabilidade bidirecional — Q2.1)
        machado.adicionarLivro(domCasmurro);
        aluisio.adicionarLivro(oCortico);
        jose.adicionarLivro(iracema);
        jorge.adicionarLivro(capitaes);

        // Composição: capítulos criados pelo próprio livro (Q3.1)
        domCasmurro.adicionarCapitulo(1, "Do título",  8);
        domCasmurro.adicionarCapitulo(2, "O agregado", 10);
        iracema.adicionarCapitulo(1, "O guerreiro",    15);

        // Agregação: biblioteca referencia livros existentes (Q3.2)
        Biblioteca biblioteca = new Biblioteca("Biblioteca Municipal", "Av. Principal, 100");
        biblioteca.adicionarLivro(domCasmurro);
        biblioteca.adicionarLivro(oCortico);
        biblioteca.adicionarLivro(iracema);
        biblioteca.adicionarLivro(capitaes);

        // Criar leitor
        Leitor joao = new Leitor("João da Silva", "123.456.789-00",
                                 "joao@email.com", "15/01/2023");

        // Simular 4 empréstimos — passamos direto os dias de atraso (Q4)
        // realizarEmprestimo(leitor, livro, diasDoEmprestimo, diasDeAtraso)
        Emprestimo.realizarEmprestimo(joao, domCasmurro, 7, 5);
        Emprestimo.realizarEmprestimo(joao, oCortico,    7, 3);
        Emprestimo.realizarEmprestimo(joao, iracema,     7, 8);
        Emprestimo.realizarEmprestimo(joao, capitaes,    7, 2);

        // --- Relatório de Multas ---
        System.out.println("--- Relatório de Multas ---");
        System.out.println("Leitor: " + joao.getNome());
        System.out.println();

        double subtotal = 0;
        int numero = 1;
        for (Emprestimo emp : joao.getEmprestimos()) {
            double multa = emp.calcularMulta();
            System.out.printf("Empréstimo %d - %s: %d dias de atraso → R$ %.2f%n",
                              numero++, emp.getLivro().getTitulo(), emp.getDiasAtraso(), multa);
            subtotal += multa;
        }

        double total = joao.calcularMultaTotal();
        System.out.println();
        System.out.printf("Subtotal: R$ %.2f%n", subtotal);
        if (total > subtotal) {
            System.out.println("Penalidade: mais de 3 atrasos → multa DOBRADA");
        }
        System.out.printf("TOTAL A PAGAR: R$ %.2f%n", total);

        // Demonstração de navegabilidade (Autor → Livros / Livro → Autor)
        System.out.println("\n--- Navegabilidade ---");
        System.out.println("Livros de " + machado.getNome() + ":");
        for (Livro l : machado.getLivros()) System.out.println("  • " + l);
        System.out.println("Autor de '" + domCasmurro + "': " + domCasmurro.getAutor().getNome());

        // Demonstração de composição (capítulos do livro)
        System.out.println("\n--- Capítulos de Dom Casmurro ---");
        for (Capitulo cap : domCasmurro.getCapitulos()) System.out.println("  " + cap);

        // Demonstração de agregação (acervo da biblioteca)
        System.out.println("\n--- Acervo ---");
        System.out.println(biblioteca);
    }
}
