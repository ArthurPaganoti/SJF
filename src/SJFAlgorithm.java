import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SJFAlgorithm {

    public static void main(String[] args) {
        // Lista de processos (exemplo fornecido)
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", 0, 5, 2));
        processes.add(new Process("P2", 2, 3, 1));
        processes.add(new Process("P3", 4, 8, 3));
        processes.add(new Process("P4", 5, 6, 2));
        processes.add(new Process("P5", 11, 8, 1));

        try {
            // Executa o algoritmo SJF e grava os resultados no arquivo
            executeSJF(processes, "results.txt");
        } catch (IOException e) {
            System.err.println("Erro ao gravar os resultados no arquivo: " + e.getMessage());
        }
    }

    public static void executeSJF(List<Process> processes, String outputFile) throws IOException {
        // Ordena os processos por tempo de chegada
        Collections.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        List<Process> executionOrder = new ArrayList<>();

        while (!processes.isEmpty()) {
            Process nextProcess = null;
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime) {
                    if (nextProcess == null || p.burstTime < nextProcess.burstTime) {
                        nextProcess = p;
                    }
                }
            }

            if (nextProcess == null) {
                // Avança o tempo caso nenhum processo esteja pronto
                currentTime++;
                continue;
            }

            // Calcula os tempos para o processo selecionado
            nextProcess.waitingTime = currentTime - nextProcess.arrivalTime;
            nextProcess.turnaroundTime = nextProcess.waitingTime + nextProcess.burstTime;

            // Atualiza o tempo atual e adiciona o processo à ordem de execução
            currentTime += nextProcess.burstTime;
            executionOrder.add(nextProcess);
            processes.remove(nextProcess);
        }

        // Calcula os tempos médios
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;
        for (Process p : executionOrder) {
            totalWaitingTime += p.waitingTime;
            totalTurnaroundTime += p.turnaroundTime;
        }
        double averageWaitingTime = totalWaitingTime / executionOrder.size();
        double averageTurnaroundTime = totalTurnaroundTime / executionOrder.size();

        // Grava os resultados no arquivo
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("Algoritmo: SJF (Shortest Job First) Não-Preemptivo\n");
            writer.write("--------------------------------------------------\n");
            writer.write("Ordem de Execução: ");
            for (Process p : executionOrder) {
                writer.write(p.id + " ");
            }
            writer.write("\n\n");

            writer.write(String.format("%-10s %-15s %-15s\n", "Processo", "Tempo de Espera", "Tempo de Retorno"));
            for (Process p : executionOrder) {
                writer.write(String.format("%-10s %-15d %-15d\n", p.id, p.waitingTime, p.turnaroundTime));
            }

            writer.write("\n");
            writer.write("Tempo Médio de Espera: " + averageWaitingTime + "\n");
            writer.write("Tempo Médio de Retorno: " + averageTurnaroundTime + "\n");
            writer.write("--------------------------------------------------\n");
        }

        // Exibe os resultados no console
        System.out.println("Resultados gravados em " + outputFile);
    }
}