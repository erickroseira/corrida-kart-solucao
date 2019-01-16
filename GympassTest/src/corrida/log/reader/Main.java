package corrida.log.reader;

import corrida.models.Piloto;
import corrida.models.VoltaCorrida;
import corrida.result.computing.ResultadoCorrida;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {


    // cria uma função lambda por meio de uma functional interface, onde recebe uma argumento (String) e produz um resultado (objeto VoltaCorrida)
    static Function<String, VoltaCorrida> novaVoltaCorrida = (linhaVoltaLog) -> {

        //fazendo split assumindo que as informações de cada coluna estarão por 2 ou mais espaços.
        String[] elementos_volta = linhaVoltaLog.split("\\s{2,}");

        //verifica se numero de colunas é igual a 5. Caso contrário termina programa
        if(elementos_volta.length != 5){
            System.out.println("Arquivo de entrada com formato diferente do especificado. Lembre-se separe colunas por 2 espaços ou mais e a informação" +
                    " de cada coluna como a identificação completa do pilto (numero - nome) por exemplo, deve estar separada por apenas 1 espaço.");
            System.exit(1);
        }

        //definindo padrão de horas
        DateTimeFormatter hora_formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        // obtem a hora da volta
        LocalTime horaVolta = LocalTime.parse(elementos_volta[0],hora_formatter);

        // obtem Piloto
        Piloto piloto = new Piloto(elementos_volta[1]);

        //obtem N° Volta
        Integer numeroVolta = Integer.parseInt(elementos_volta[2]);

        //obtem Tempo Volta. OBS: (transforma string Tempo da volta no formato HH:mm:ss.SSS)
        String voltaLap = (elementos_volta[3].split(":")[0].length() < 2) ? "00:0"+elementos_volta[3] : "00:"+elementos_volta[3];
        LocalTime tempoVolta = LocalTime.parse(voltaLap,hora_formatter);

        //obtem Velocidade média da volta. OBS: (linha abaixo verifica se velocidade está separada por virgula. Se sim, substitui por ponto (.)
        String velocidade = (elementos_volta[4].contains(",")) ? elementos_volta[4].replace(",",".") : elementos_volta[4];
        Float velMedia = Float.parseFloat(velocidade);

        //retorna novo objeto VoltaCorrida
        return new VoltaCorrida(horaVolta, piloto, numeroVolta, tempoVolta, velMedia);
    };

    public static void main(String[] args) {

        // verifica se o numero de argumento é igual ao 2.
        if(args.length == 2){

            List<VoltaCorrida> listaVoltasCorrida = new ArrayList<>();

            // caminho arquivo de entrada
            String arquivoDeEntradaPath = args[0];

            // Lê linhas do arquivo de entrada
            try (Stream<String> stream = Files.lines(Paths.get(arquivoDeEntradaPath))){

                // para cada linha (excluindo a primeira) transforme-a propriamente para um objeto do tipo VoltaCorrida e ao final colete estes objetos em uma lista
                listaVoltasCorrida = stream.filter(linha -> !linha.contains("Hora")).map(linha-> novaVoltaCorrida.apply(linha)).collect(Collectors.toList());

            } catch (IOException e) {
                e.printStackTrace();
            }

            // instancia classe responsavel por calcular resultado da corrida
            ResultadoCorrida resultado = new ResultadoCorrida();

            // inicia o processo de cálculo do resultado final da corrida
            String relatorio = resultado.CalculaResultadoCorrida(listaVoltasCorrida);


            // printa resultado no console.
            System.out.println(relatorio);

            // As linhas abaixo salvam o relatorio final no caminho especificado
            String relatorioFinalPath = args[1];
            try {
                Files.write(Paths.get(relatorioFinalPath), relatorio.getBytes());
            } catch (IOException e) {
                System.out.println("Não foi possivel salvar o relatorio final no caminho especificado. Talvez o caminho esteja incorreto. Tente novamente");
                e.printStackTrace();
            }

        }else{
            System.out.println("É necessário informar caminho do arquivo de entrada (log da corrida) e o caminho onde salvar o relatorio final");
        }

    }
}
