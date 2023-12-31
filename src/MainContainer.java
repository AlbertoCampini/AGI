import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.util.HashMap;

public class MainContainer {
    public static void main(String[] args) {
        jade.core.Runtime rt = jade.core.Runtime.instance();
        Profile p = new ProfileImpl();
        ContainerController cc = rt.createMainContainer(p);

        try {
            // Creazione agente cuoco
            AgentController cuocoController = cc.createNewAgent("cuoco", CuocoAgent.class.getName(), null);
            cuocoController.start();

            // Creazione agenti cameriere
            int numCamerieri = 2; // Numero di agenti camerieri da creare
            for (int i = 0; i < numCamerieri; i++) {
                AgentController cameriereController = cc.createNewAgent("cameriere" + (i + 1),
                        CameriereAgent.class.getName(), null);
                cameriereController.start();
            }

            // Creazione agenti clienti
            int numClienti = 5; // Numero di agenti clienti da creare
            int count = 0;
            for (int i = 0; i < numClienti; i++) {
                AgentController clienteController = cc.createNewAgent("cliente" + (count + 1),
                        ClienteAgent.class.getName(), null);
                clienteController.start();
                count++;
            }
            /*
            try {
                Thread.sleep(20000); // Simula 20 per l'ingresso di un nuovo tavolo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Arrivano altri clienti...
            for (int i = 0; i < numClienti; i++) {
                AgentController clienteController = cc.createNewAgent("cliente" + (count + 1),
                        ClienteAgent.class.getName(), null);
                clienteController.start();
                count++;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
