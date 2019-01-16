package corrida.models;

/*
* Classe que representa o cada piloto participante na corrida
 */
public class Piloto {

    private String nomePiloto;

    public Piloto (String nome){
        this.nomePiloto = nome;
    }

    public String getNomePiloto() { return nomePiloto; }

    public void setNomePiloto(String nomePiloto) { this.nomePiloto = nomePiloto; }

}
