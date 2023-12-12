package cl.ucn.felix.biblioteca.servicio.api;

public class ExcepcionSesionNoValidaTiempoEjecucion extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ExcepcionSesionNoValidaTiempoEjecucion(String sesion) {
		super("Sesion no se pudo validar validada " + sesion +"\n ERROR TIEMPO DE EJECUCION ");
	}
}
