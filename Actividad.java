import java.util.ArrayList;
import java.util.List;

public class Actividad {
    public String id;
    public String preds; 
    public double tOptimista, tProbable, tPesimista;
    public double tEsperado, varianza, holgura;
    //Tiempos para calcular ruta critica
    public double iTemprano, tTemprano;
    public double iTardio, tTardio;
    public boolean esCritica;
     // Listas para manejar dependencias
    public List<Actividad> predecesoras;
    public List<Actividad> sucesoras;

    // Constructor
    public Actividad(String id, String preds, double tOptimista, double tProbable, double tPesimista) {
        this.id = id;
        this.preds = preds;
        this.tOptimista = tOptimista;
        this.tProbable = tProbable;
        this.tPesimista = tPesimista;
        
        this.predecesoras = new ArrayList<>();
        this.sucesoras = new ArrayList<>();
        this.esCritica = false;

        // Cálculo del Tiempo Esperado (Te)
        this.tEsperado = (tOptimista + 4 * tProbable + tPesimista) / 6.0;
    }
}
