import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class MonitorDormilonSimulador {
    public static void main(String[] args) {
        MonitorDormilon monitor = new MonitorDormilon();

       Thread monitorThread = new Thread(() -> monitor.atenderEstudiantes());
        monitorThread.start();

        for (int i = 1; i <= 10; i++) {
            Thread estudianteThread = new Thread(new Estudiante(monitor, i));
            estudianteThread.start();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class MonitorDormilon {
    private Semaphore sillaEspera;
    private Semaphore monitorListo;
    private Semaphore sillaMonitor;
    private Queue<Integer> colaStudents;

    public MonitorDormilon() {
        sillaEspera = new Semaphore(3);
        monitorListo = new Semaphore(0);
        sillaMonitor = new Semaphore(0);
        colaStudents = new LinkedList<>();
    }

    public void pedirAyuda(int id) {
        try {
            if (!sillaEspera.tryAcquire()) {
                System.out.println("Estudiante " + id + ": No hay sillas, me voy a programar a la sala de cómputo");
                Thread.sleep(3000);
                return;
            }

            synchronized (colaStudents) {
                colaStudents.add(id);
            }

            System.out.println("Estudiante " + id + ": Estoy en una silla del corredor");

            sillaMonitor.release();
            monitorListo.acquire();

            System.out.println("Estudiante " + id + ": Estoy con el monitor");
            Thread.sleep(2000);
            System.out.println("Estudiante " + id + ": Terminé de recibir ayuda");

            sillaEspera.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
   
}



