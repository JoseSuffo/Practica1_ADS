package Clases;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ControladorTest {

    @Test
    void testControladorInstanciacion() {
        ControladorMenu controlador = new ControladorMenu();
        assertNotNull(controlador, "El controlador debería poder instanciarse.");
    }
}
