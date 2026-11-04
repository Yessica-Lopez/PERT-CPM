import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Proyecto {
    private List<Actividad> listaActividades;
    private double duracionProyecto;

    public Proyecto() {
        listaActividades = new ArrayList<>();
        duracionProyecto = 0;
        iniciarProyecto();
    }

    public void iniciarProyecto() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Ingrese la cantidad de actividades: ");
        int n = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer

        // 1. Lectura de actividades y sus tiempos
        for (int i = 0; i < n; i++) {
            System.out.println("\n--- Actividad " + (i + 1) + " ---");
            System.out.print("Nombre/ID de la actividad: ");
            String id = scanner.nextLine();
            
            System.out.print("Tiempo Optimista (O): ");
            double o = scanner.nextDouble();
            System.out.print("Tiempo Más Probable (M): ");
            double m = scanner.nextDouble();
            System.out.print("Tiempo Pesimista (P): ");
            double p = scanner.nextDouble();
            scanner.nextLine(); // Limpiar el buffer

            Actividad act = new Actividad(id, o, m, p);
            listaActividades.add(act);
        }

        // 2. Definir predecesoras para cada actividad
        for (Actividad act : listaActividades) {
            System.out.print("Ingrese las predecesoras de la actividad " + act.id + " separadas por comas (o presione Enter si no tiene): ");
            String preds = scanner.nextLine();
            if (!preds.trim().isEmpty()) {
                String[] nombresPreds = preds.split(",");
                for (String nombrePred : nombresPreds) {
                    nombrePred = nombrePred.trim();
                    Actividad pAct = buscarActividad(nombrePred);
                    if (pAct != null) {
                        act.predecesoras.add(pAct);
                        pAct.sucesoras.add(act);
                    }
                }
            }
        }

        // Ejecutar los cálculos del método
        ejecutarPaseAdelante();
        ejecutarPaseAtras();
        calcularHolgurasYrutaCritica();
        mostrarResultados();
    }

    private Actividad buscarActividad(String id) {
        for (Actividad a : listaActividades) {
            if (a.id.equalsIgnoreCase(id)) {
                return a;
            }
        }
        return null;
    }

    private void ejecutarPaseAdelante() {
        for (Actividad act : listaActividades) {
            if (act.predecesoras.isEmpty()) {
                act.iTemprano = 0; 
            } else {
                double maxFt = 0;
                for (Actividad pred : act.predecesoras) {
                    if (pred.tTemprano > maxFt) {
                        maxFt = pred.tTemprano;
                    }
                }
                act.iTemprano = maxFt;  // El Inicio Temprano es el máximo de los Fines Tempranos predecesores
            }
            act.tTemprano = act.iTemprano + act.tEsperado; // Fin Temprano
            
            if (act.tTemprano > duracionProyecto) {
                duracionProyecto = act.tTemprano;
            }
        }
    }

    private void ejecutarPaseAtras() {
        for (int i = listaActividades.size() - 1; i >= 0; i--) {
            Actividad act = listaActividades.get(i);
            if (act.sucesoras.isEmpty()) {
                act.tTardio = duracionProyecto; // Última actividad
            } else {
                double minItar = Double.MAX_VALUE;
                for (Actividad suc : act.sucesoras) {
                    if (suc.iTardio < minItar) {
                        minItar = suc.iTardio;
                    }
                }
                act.tTardio = minItar; // El Fin Tardío es el mínimo de los Inicios Tardíos sucesores
            }
            act.iTardio = act.tTardio - act.tEsperado; // Inicio Tardío
        }
    }

    private void calcularHolgurasYrutaCritica() {
        for (Actividad act : listaActividades) {
            act.holgura = act.tTardio - act.tTemprano; 
            if (Math.abs(act.holgura) < 0.001) {
                act.esCritica = true; 
            }
        }
    }

    private void mostrarResultados() {
        System.out.println("\n==RESULTADOS PERT-CPM ==");
        System.out.printf("%-10s | %-6s | %-6s | %-6s | %-6s | %-6s | %-8s | %-8s\n", 
                "Actividad", "Te", "Ti", "FT", "ITar", "FTar", "Holgura", "Crítico");
        System.out.println("-------------------------------------------------------------------------------------");
        
        for (Actividad act : listaActividades) {
            System.out.printf("%-10s | %-6.2f | %-6.2f | %-6.2f | %-6.2f | %-6.2f | %-8.2f | %-8s\n",
                    act.id, act.tEsperado, act.iTemprano, act.tTemprano, act.iTardio, act.tTardio, act.holgura, (act.esCritica ? "Sí" : "No"));
        }
        
        System.out.println("\nDuración total del proyecto: " + duracionProyecto);
        
        System.out.print("Ruta Crítica: ");
        List<String> ruta = new ArrayList<>();
        for (Actividad act : listaActividades) {
            if (act.esCritica) {
                ruta.add(act.id);
            }
        }
        System.out.println(String.join(" -> ", ruta));
        System.out.println("=====================================================================================");
    }
}