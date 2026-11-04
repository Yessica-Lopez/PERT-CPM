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
    
    public static void main(String[] args){
        Proyecto proyectoPERT = new Proyecto();

    }



    public void iniciarProyecto() {
       Scanner scanner = new Scanner(System.in);

    System.out.print("Ingrese la cantidad de actividades: ");
    int n = Integer.parseInt(scanner.nextLine().trim());

    System.out.println("Formato: ID predecesoras O M P . Use - si no tiene.");
    List<String> predsTexto = new ArrayList<>();

    
    for (int i = 0; i < n; i++) {
        String[] partes = scanner.nextLine().trim().split("\\s+");
        String id = partes[0];
        String preds = partes[1];
        double o = Double.parseDouble(partes[2]);
        double m = Double.parseDouble(partes[3]);
        double p = Double.parseDouble(partes[4]);

        listaActividades.add(new Actividad(id, o, m, p));
        predsTexto.add(preds);
    }

    //Enlazar predecesoras
    for (int i = 0; i < n; i++) {
        Actividad act = listaActividades.get(i);
        String preds = predsTexto.get(i);
        if (!preds.equals("-")) {
            for (String nombrePred : preds.split(",")) {
                Actividad pAct = buscarActividad(nombrePred.trim());
                if (pAct != null) {
                    act.predecesoras.add(pAct);
                    pAct.sucesoras.add(act);
                }
            }
        }
    }

    //Ejecutar los cálculos del método
    ordenarPorDependencias();
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
    private void ordenarPorDependencias() {
    List<Actividad> ordenada = new ArrayList<>();
    List<Actividad> pendientes = new ArrayList<>(listaActividades);

    while (!pendientes.isEmpty()) {
        boolean avanzo = false;
        for (Actividad act : new ArrayList<>(pendientes)) {
            if (ordenada.containsAll(act.predecesoras)) {
                ordenada.add(act);
                pendientes.remove(act);
                avanzo = true;
            }
        }
        if (!avanzo) {
            System.out.println("Error: hay un ciclo en las dependencias.");
            System.exit(1);
        }
    }
    listaActividades = ordenada;
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
                act.iTemprano = maxFt;  //El Inicio Temprano es el maximo de los Fines Tempranos predecesores
            }
            act.tTemprano = act.iTemprano + act.tEsperado; //Fin Temprano
            
            if (act.tTemprano > duracionProyecto) {
                duracionProyecto = act.tTemprano;
            }
        }
    }

    private void ejecutarPaseAtras() {
        for (int i = listaActividades.size() - 1; i >= 0; i--) {
            Actividad act = listaActividades.get(i);
            if (act.sucesoras.isEmpty()) {
                act.tTardio = duracionProyecto; //Ultima actividad
            } else {
                double minItar = Double.MAX_VALUE;
                for (Actividad suc : act.sucesoras) {
                    if (suc.iTardio < minItar) {
                        minItar = suc.iTardio;
                    }
                }
                act.tTardio = minItar; //El Fin Tardio es el minimo de los inicios Tardios sucesores
            }
            act.iTardio = act.tTardio - act.tEsperado; //Inicio Tardio
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