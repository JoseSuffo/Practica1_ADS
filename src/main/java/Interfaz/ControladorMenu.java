package Interfaz;

import Lógica.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Optional;

public class ControladorMenu {
    private PersonaService personaService = new PersonaService();

    @FXML
    public void botonAltas() {
        Optional<String> nombreOpt = pedirEntrada("Nueva Alta", "Nombre de la persona:");

        if (nombreOpt.isPresent() && !nombreOpt.get().trim().isEmpty()) {
            Persona p = new Persona(nombreOpt.get());

            boolean añadirMasDir = true;
            while (añadirMasDir) {
                Optional<String> dirOpt = pedirEntrada("Añadir Dirección",
                        "Dirección nº " + (p.getDirecciones().size() + 1) + ":");

                if (dirOpt.isPresent() && !dirOpt.get().trim().isEmpty()) {
                    p.addDireccion(new Direccion(dirOpt.get()));

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Deseas añadir OTRA dirección?", ButtonType.YES, ButtonType.NO);
                    if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.NO) {
                        añadirMasDir = false;
                    }
                } else {
                    añadirMasDir = false;
                }
            }

            boolean añadirMasTlf = true;
            while (añadirMasTlf) {
                Optional<String> tlfOpt = pedirEntrada("Añadir Teléfono",
                        "Teléfono nº " + (p.getTelefonos().size() + 1) + ":");

                if (tlfOpt.isPresent() && !tlfOpt.get().trim().isEmpty()) {
                    p.addTelefono(tlfOpt.get());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Deseas añadir OTRO teléfono?", ButtonType.YES, ButtonType.NO);
                    if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.NO) {
                        añadirMasTlf = false;
                    }
                } else {
                    añadirMasTlf = false;
                }
            }

            try {
                personaService.registrarNuevaPersona(p);
                mostrarAlerta("Éxito", "Persona guardada correctamente con sus datos.");
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo completar el registro: " + e.getMessage());
            }
        }
    }

    @FXML
    public void botonBajas() {
        Optional<String> idOpt = pedirEntrada("Baja", "ID de la persona:");
        idOpt.ifPresent(idStr -> {
            try {
                int id = Integer.parseInt(idStr);
                personaService.eliminarPersona(id);
                mostrarAlerta("Éxito", "Registro eliminado.");
            } catch (Exception e) {
                mostrarAlerta("Error", e.getMessage());
            }
        });
    }

    @FXML
    public void botonModificaciones() {
        Optional<String> idOpt = pedirEntrada("Modificar", "ID de la persona:");
        if (idOpt.isPresent()) {
            Optional<String> nomOpt = pedirEntrada("Nuevo Nombre", "Introduce el nombre:");
            nomOpt.ifPresent(nuevoNombre -> {
                try {
                    personaService.actualizarNombrePersona(Integer.parseInt(idOpt.get()), nuevoNombre);
                    mostrarAlerta("Éxito", "Nombre actualizado.");
                } catch (Exception e) {
                    mostrarAlerta("Error", e.getMessage());
                }
            });
        }
    }

    @FXML
    public void botonConsultas() {
        try {
            String datos = personaService.obtenerListadoCompleto();
            mostrarAlerta("Listado de Agenda", datos.isEmpty() ? "No hay registros." : datos);
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    private Optional<String> pedirEntrada(String titulo, String contenido) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(titulo);
        dialog.setHeaderText(null);
        dialog.setContentText(contenido);
        return dialog.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        if (mensaje.length() > 200) {
            TextArea ta = new TextArea(mensaje);
            ta.setEditable(false);
            alert.getDialogPane().setContent(ta);
        } else {
            alert.setContentText(mensaje);
        }
        alert.showAndWait();
    }
}