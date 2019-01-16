package corrida.result.computing;

import corrida.models.VoltaCorrida;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/*
* Classe responsavel por calcular o resultado da corrida. Cada saída do relatorio é gerado por
* uma função específica abaixo especificada
 */
public class ResultadoCorrida {

    //String builder que conterá relatorio final
    private StringBuilder relatorio_Final = new StringBuilder();

    /*
     * Função que computa posição final (classficação final) dos pilotos
      */
    public HashMap<String, DadosCorridaPiloto> computaPosicaoFinalDosPilotos(HashMap<String, DadosCorridaPiloto> pre_posicao_pilotos){

        //hashmap que sera retornado. OBS é um LinkedHashMap pois ele sempre preserva a ordem de insercao
        HashMap<String, DadosCorridaPiloto> pilotos_ordenados = new LinkedHashMap<>();

        //ordena a lista primeiramente pela numero de voltas de cada piloto.
        // Havendo pilotos terminando a corrida com o mesmo numero de voltas verifica quem chegou primeiro
        pre_posicao_pilotos.entrySet().stream().sorted(Map.Entry.comparingByValue(
                Comparator.comparingInt(DadosCorridaPiloto::getNumeroDeVoltasTotais).reversed()
                        .thenComparing(DadosCorridaPiloto::getHoraUltimaVolta)))
                .forEachOrdered(piloto -> pilotos_ordenados.put(piloto.getKey(), piloto.getValue()));

        return pilotos_ordenados;
    }


    // função que calcula e faz chamadas para métodos que ajudarão a construir o relatório final
    public String CalculaResultadoCorrida(List<VoltaCorrida> listaVoltas){

        // hash map que conterá dados relevantes de cada piloto ao final da corrida
        HashMap<String, DadosCorridaPiloto> dados_pilotos_final_da_corrida = new HashMap<>();

        // agrupa voltas por cada piloto
        Map<String, List<VoltaCorrida>> voltas_GroupedBy_Piloto = listaVoltas.stream().collect(Collectors.groupingBy(volta-> volta.getPiloto().getNomePiloto()));

        // para cada piloto, itere sobre suas voltas
        for (Map.Entry<String, List<VoltaCorrida>> piloto : voltas_GroupedBy_Piloto.entrySet()) {

            // recupera voltas do piloto corrente
            List<VoltaCorrida> piloto_voltas = piloto.getValue();

            //variavel responsável por acumular as velocidades de cada volta do piloto atual
            Float somadorVelocidades = 0.0F;

            //para cada volta do piloto atual
            for(VoltaCorrida volta: piloto_voltas){

                //se o piloto ainda não está no hashmap adicione-o
                if(!dados_pilotos_final_da_corrida.containsKey(piloto.getKey())) {

                    somadorVelocidades += volta.getVelocidadeMediaVolta();
                    dados_pilotos_final_da_corrida.put(piloto.getKey(), new DadosCorridaPiloto(volta.getHoraVolta(), volta.getNumeroVolta(), volta.getTempoVolta(), volta.getVelocidadeMediaVolta()));

                }else{

                    //se o piloto já esta no hashmap dados_pilotos_final_da_corrida, atualize seus dados//

                    DadosCorridaPiloto dados_piloto_corrente = dados_pilotos_final_da_corrida.get(piloto.getKey());

                    //atualiza hora da última volta
                    dados_piloto_corrente.setHoraUltimaVolta(volta.getHoraVolta());
                    //atualiza número da volta
                    dados_piloto_corrente.setNumeroDeVoltasTotais(volta.getNumeroVolta());
                    //atualiza velocidadeTotal
                    dados_piloto_corrente.setVelocidadeTotalCorrida(dados_piloto_corrente.getVelocidadeTotalCorrida() + volta.getVelocidadeMediaVolta());
                    //atualiza tempo total durante a corrida
                    dados_piloto_corrente.setTempoTotalCorrida(dados_piloto_corrente.somarLocalTimes(dados_piloto_corrente.getTempoTotalCorrida(), volta.getTempoVolta()));

                    //se tempo da volta atual for melhor (menor) que o anterior, atualize este valor
                    if(volta.getTempoVolta().isBefore(dados_piloto_corrente.getMelhorVolta()))
                        dados_piloto_corrente.setMelhorVolta(volta.getTempoVolta());


                    // variavel que armazena a soma das velocidades de cada volta do piloto atual
                    somadorVelocidades += volta.getVelocidadeMediaVolta();

                    // se for a última volta do piloto atual calcule sua Velocidade Média
                    if(volta.equals(piloto_voltas.get(piloto_voltas.size() - 1))) {

                        Float velocidadeMedia = somadorVelocidades / piloto_voltas.size();
                        dados_piloto_corrente.setVelocidadeMediaCorrida(velocidadeMedia);

                        somadorVelocidades = 0.0F; // reseta somadorVelocidades
                    }

                    //atualiza dados do piloto atual no hashmap dados_pilotos_final_da_corrida
                    dados_pilotos_final_da_corrida.put(piloto.getKey(), dados_piloto_corrente);
                }

            }

        }

        //hash map com classificação final dos pilotos
        HashMap<String, DadosCorridaPiloto> posicao_final = computaPosicaoFinalDosPilotos(dados_pilotos_final_da_corrida);

        //gera relatório da classificação final da corrida
        classificacaoFinalPilotos(posicao_final);

        //gera relatório da melhor volta de cada piloto na corrida
        melhorVoltaDeCadaPiloto(dados_pilotos_final_da_corrida);

        //gera relatório da melhor volta da corrida
        melhorVoltaDaCorrida(dados_pilotos_final_da_corrida);

        //gera relatório da velocidade média de cada piloto na corrida
        velocidadeMediaDeCadaPiloto(dados_pilotos_final_da_corrida);

        return relatorio_Final.toString();
    }

    // função que desenha "-----------..." em cima e em baixo dos nomes das colunas do relatorio final
    // apenas formatação
    private String linhaFormatadoraColunasTabelas(StringBuilder stringbuilder){
        return String.join("", Collections.nCopies(stringbuilder.toString().length(), "-"));
    }

    private void melhorVoltaDeCadaPiloto(HashMap<String, DadosCorridaPiloto> dados_pilotos_ultima_volta){

        StringBuilder melhor_volta_by_piloto_relatorio = new StringBuilder();

        // formata string
        Formatter fmt = new Formatter(melhor_volta_by_piloto_relatorio);
        fmt.format("\n|%1$-35s|%2$-35s|%3$-35s|\n","Código Piloto","Nome Piloto", "Melhor Volta");

        String primeiraLinha = linhaFormatadoraColunasTabelas(melhor_volta_by_piloto_relatorio);
        melhor_volta_by_piloto_relatorio.insert(0,primeiraLinha );
        melhor_volta_by_piloto_relatorio.append(primeiraLinha).append(System.lineSeparator());

        // constrói  relatório da melhor Volta De Cada Piloto
        dados_pilotos_ultima_volta.forEach((piloto, dadosCorrida)->{

            String codigo_piloto = piloto.split(" – ")[0];
            String nome_piloto = piloto.split(" – ")[1];
            LocalTime melhor_volta = dadosCorrida.getMelhorVolta();

            fmt.format("|%1$-35s|%2$-35s|%3$-35s|\n",codigo_piloto, nome_piloto, melhor_volta.toString());

        });

        //acrescenta no relatório Final título como: [ MELHOR VOLTA POR PILOTO ]
        relatorio_Final.append(System.lineSeparator()).append(System.lineSeparator()).append("-> [ MELHOR VOLTA POR PILOTO ]").append(System.lineSeparator()).append(System.lineSeparator());
        //append no relatório Final dados sobre MELHOR VOLTA POR PILOTO
        relatorio_Final.append(melhor_volta_by_piloto_relatorio.toString());
    }

    private void melhorVoltaDaCorrida(HashMap<String, DadosCorridaPiloto> dados_pilotos_ultima_volta){

        StringBuilder melhor_volta_da_corrida = new StringBuilder();

        Formatter fmt = new Formatter(melhor_volta_da_corrida);
        fmt.format("\n|%1$-35s|%2$-35s|%3$-35s|\n","Código Piloto","Nome Piloto", "Tempo da Volta");

        String primeiraLinha = linhaFormatadoraColunasTabelas(melhor_volta_da_corrida);
        melhor_volta_da_corrida.insert(0,primeiraLinha );
        melhor_volta_da_corrida.append(primeiraLinha).append(System.lineSeparator());


        //hashmap que sera retornado. OBS é um LinkedHashMap pois ele sempre preserva a ordem de insercão
        //pilotos ordenados pelo menor tempo de volta
        LinkedHashMap<String, DadosCorridaPiloto> pilotos_ordenados_menor_tempo_volta = new LinkedHashMap<>();

        //ordena a hashmap pelo menor volta de cada piloto
        dados_pilotos_ultima_volta.entrySet().stream().sorted(Map.Entry.comparingByValue(
                Comparator.comparing(DadosCorridaPiloto::getMelhorVolta)))
                .forEachOrdered(piloto -> pilotos_ordenados_menor_tempo_volta.put(piloto.getKey(), piloto.getValue()));

        DadosCorridaPiloto melhor_piloto = pilotos_ordenados_menor_tempo_volta.values().iterator().next();

        String codigo_piloto = pilotos_ordenados_menor_tempo_volta.keySet().iterator().next().split(" – ")[0];
        String nome_piloto = pilotos_ordenados_menor_tempo_volta.keySet().iterator().next().split(" – ")[1];
        LocalTime melhor_volta = melhor_piloto.getMelhorVolta();

        fmt.format("|%1$-35s|%2$-35s|%3$-35s|\n",codigo_piloto, nome_piloto, melhor_volta.toString());

        //acrescenta no relatorio Final título seção MELHOR VOLTA POR PILOTO
        relatorio_Final.append(System.lineSeparator()).append(System.lineSeparator()).append("-> [ MELHOR VOLTA DA CORRIDA ]").append(System.lineSeparator()).append(System.lineSeparator());
        //append no relatorio Final dados sobre MELHOR VOLTA POR PILOTO
        relatorio_Final.append(melhor_volta_da_corrida.toString());

    }

    private void velocidadeMediaDeCadaPiloto(HashMap<String, DadosCorridaPiloto> dados_pilotos_ultima_volta){

        StringBuilder velocidade_media_por_piloto = new StringBuilder();

        Formatter fmt = new Formatter(velocidade_media_por_piloto);
        fmt.format("\n|%1$-35s|%2$-35s|%3$-35s|\n","Código Piloto","Nome Piloto", "Velocidade Média");

        String primeiraLinha = linhaFormatadoraColunasTabelas(velocidade_media_por_piloto);
        velocidade_media_por_piloto.insert(0,primeiraLinha );
        velocidade_media_por_piloto.append(primeiraLinha).append(System.lineSeparator());

        // constroi  relatorio da velocidade Média de Cada Piloto
        dados_pilotos_ultima_volta.forEach((piloto, dadosCorrida)->{

            fmt.format("|%1$-35s|%2$-35s|%3$-35s|\n",piloto.split(" – ")[0], piloto.split(" – ")[1], dadosCorrida.getVelocidadeMediaCorrida().toString());

        });

        //acrescenta no relatorio Final título seção Velcidade Média por Piloto
        relatorio_Final.append(System.lineSeparator()).append(System.lineSeparator()).append("-> [ VELOCIDADE MÉDIA POR PILOTO ]").append(System.lineSeparator()).append(System.lineSeparator());
        //append no relatorio Final dados sobre MELHOR VOLTA POR PILOTO
        relatorio_Final.append(velocidade_media_por_piloto.toString());


    }

    // função auxiliar para calcular diferenças entre localtimes
    public LocalTime subtrairLocalTimes(LocalTime lt1, LocalTime lt2){

        LocalTime localTime = lt1.minusHours(lt2.getHour()); //subtrai horas
        localTime = localTime.minusMinutes(lt2.getMinute()); //subtrai minutos
        localTime = localTime.minusSeconds(lt2.getSecond()); //subtrai segundos
        localTime = localTime.minusNanos(lt2.getNano()); //subtrai nanosegundos

        return localTime;

    }

    private void classificacaoFinalPilotos(HashMap<String, DadosCorridaPiloto> posicao_final){

        StringBuilder saida = new StringBuilder();

        Formatter fmt = new Formatter(saida);
        fmt.format("\n|%1$-30s|%2$-30s|%3$-30s|%4$-30s|%5$-30s|%6$-30s\n","Posição Chegada", "Código Pilto","Nome Piloto", "Qtde Voltas Completadas", "Tempo Total de Prova", "Tempo de Chegada Após Vencedor");

        String primeiraLinha = linhaFormatadoraColunasTabelas(saida);
        saida.insert(0,primeiraLinha );
        saida.append(primeiraLinha).append("\n");


        LocalTime tempo_chega_vencedor = posicao_final.values().iterator().next().getHoraUltimaVolta();

        // constrói  relatorio da classificação final de cada piloto
        posicao_final.forEach((piloto, dadosCorridaPiloto)->{

            int count = 1;

            String codigo_piloto = piloto.split(" – ")[0];
            String nome_piloto = piloto.split(" – ")[1];
            Integer qtd_voltas = dadosCorridaPiloto.getNumeroDeVoltasTotais();
            String tempo_total = dadosCorridaPiloto.getTempoTotalCorrida().toString();
            LocalTime hora_chegada = dadosCorridaPiloto.getHoraUltimaVolta();

            String diferenca_to_vencedor = (tempo_chega_vencedor.compareTo(hora_chegada) == 0)? "(Não se Aplica)" : subtrairLocalTimes(hora_chegada, tempo_chega_vencedor).toString();

            fmt.format("|%1$-30s|%2$-30s|%3$-30s|%4$-30s|%5$-30s|%6$-30s\n",count++,codigo_piloto, nome_piloto, qtd_voltas, tempo_total, diferenca_to_vencedor);

        });


        relatorio_Final.append("[ RESULTADO FINAL DA CORRIDA (CLASSIFICAÇÃO) E DIFERENÇA DE TEMPO DE CHEGADA DE CADA PILOTO PARA O VENCEDOR ]").append(System.lineSeparator()).append(System.lineSeparator());
        relatorio_Final.append(saida.toString());

    }


}
