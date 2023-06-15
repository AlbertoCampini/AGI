import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

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
            int numCamerieri = 1; // Numero di agenti camerieri da creare
            for (int i = 0; i < numCamerieri; i++) {
                AgentController cameriereController = cc.createNewAgent("cameriere" + (i + 1),
                        CameriereAgent.class.getName(), null);
                cameriereController.start();
            }

            // Creazione agenti clienti
            int numClienti = 5; // Numero di agenti clienti da creare
            for (int i = 0; i < numClienti; i++) {
                AgentController clienteController = cc.createNewAgent("cliente" + (i + 1),
                        ClienteAgent.class.getName(), null);
                clienteController.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
