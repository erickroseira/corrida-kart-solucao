package corrida.result.computing;
import java.time.LocalTime;

/*
*   Classe que representa dados finais de cada piloto na corrida, como por exemplo
*   velocidade média, tempo total da corrida, melhor volta, numeros de voltas, etc..
 */

public class DadosCorridaPiloto {

    private LocalTime horaUltimaVolta;
    private Integer numeroDeVoltasTotais;
    private LocalTime tempoTotalCorrida;
    private Float velocidadeTotalCorrida;
    private LocalTime melhorVolta;
    private Float velocidadeMediaCorrida;



    public DadosCorridaPiloto(LocalTime horaUltimaVolta, Integer numeroDeVoltasTotais, LocalTime tempoTotalCorrida, Float velocidadeTotalCorrida) {
        this.horaUltimaVolta = horaUltimaVolta;
        this.numeroDeVoltasTotais = numeroDeVoltasTotais;
        this.tempoTotalCorrida = tempoTotalCorrida;
        this.velocidadeTotalCorrida = velocidadeTotalCorrida;

        //assume que a melhor volta é a primeira. Depois esta informação é verificado
        // posteriormente com os dados das voltas posteriores de cada piloto
        this.melhorVolta = tempoTotalCorrida;
    }

    // função auxiliar para somar localTimes
    public LocalTime somarLocalTimes(LocalTime lt1, LocalTime lt2){

        LocalTime localTime = lt1.plusHours(lt2.getHour()); //soma horas
        localTime = localTime.plusMinutes(lt2.getMinute()); //soma minutos
        localTime = localTime.plusSeconds(lt2.getSecond()); //soma segundos
        localTime = localTime.plusNanos(lt2.getNano()); //soma nanosegundos

        return localTime;

    }

    //getters and setters

    public LocalTime getHoraUltimaVolta() { return horaUltimaVolta;  }

    public void setHoraUltimaVolta(LocalTime horaUltimaVolta){ this.horaUltimaVolta = horaUltimaVolta;    }

    public Integer getNumeroDeVoltasTotais() { return numeroDeVoltasTotais; }

    public void setNumeroDeVoltasTotais(Integer numeroDeVoltasTotais) { this.numeroDeVoltasTotais = numeroDeVoltasTotais; }

    public LocalTime getTempoTotalCorrida() { return tempoTotalCorrida;  }

    public void setTempoTotalCorrida(LocalTime tempoTotalCorrida){ this.tempoTotalCorrida = tempoTotalCorrida;  }

    public Float getVelocidadeTotalCorrida() { return velocidadeTotalCorrida;  }

    public void setVelocidadeTotalCorrida(Float velocidadeMediaCorrida) {  this.velocidadeTotalCorrida = velocidadeMediaCorrida; }

    public Float getVelocidadeMediaCorrida() { return velocidadeMediaCorrida; }

    public void setVelocidadeMediaCorrida(Float velocidadeMediaCorrida){  this.velocidadeMediaCorrida = velocidadeMediaCorrida; }

    public LocalTime getMelhorVolta() { return melhorVolta; }

    public void setMelhorVolta(LocalTime melhorVolta) { this.melhorVolta = melhorVolta; }

}
