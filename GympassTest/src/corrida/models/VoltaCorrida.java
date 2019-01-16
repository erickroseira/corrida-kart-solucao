package corrida.models;

import java.time.LocalTime;


/*
*   Classe que representa cada linha do log da corrida (arquivo de entrada)
 */

public class VoltaCorrida {


    private LocalTime horaVolta;
    private Piloto piloto;
    private Integer numeroVolta;
    private LocalTime tempoVolta;
    private Float velocidadeMediaVolta;

    public VoltaCorrida(LocalTime horaVolta, Piloto piloto, int numeroVolta, LocalTime tempoVolta, Float velocidadeMediaVolta) {
        this.piloto = piloto;
        this.numeroVolta = numeroVolta;
        this.horaVolta = horaVolta;
        this.tempoVolta = tempoVolta;
        this.velocidadeMediaVolta = velocidadeMediaVolta;
    }

    //getters and setters

    public Piloto getPiloto() { return piloto; }

    public int getNumeroVolta() { return numeroVolta; }

    public LocalTime getHoraVolta() { return horaVolta; }

    public LocalTime getTempoVolta() { return tempoVolta; }

    public Float getVelocidadeMediaVolta() { return velocidadeMediaVolta; }


}
