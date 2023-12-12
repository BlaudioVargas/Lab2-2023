package cl.ucn.felix.biblioteca.servicio.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import cl.ucn.felix.biblioteca.api.ExcepcionLibroInvalido;
import cl.ucn.felix.biblioteca.api.ExcepcionLibroNoEncontrado;
import cl.ucn.felix.biblioteca.api.Inventario;
import cl.ucn.felix.biblioteca.api.Inventario.CriterioBusqueda;
import cl.ucn.felix.biblioteca.api.Libro;

import cl.ucn.felix.biblioteca.api.impl.InventarioImpl;
import cl.ucn.felix.biblioteca.api.impl.LibroMutableImpl;

public class ServicioInventarioLibroImpl implements ServicioInventarioLibro{

	private String sesion;
	private BundleContext contexto;
	
	
	public ServicioInventarioLibroImpl(BundleContext contexto) {
		
		this.contexto = contexto;
	}

		
	@Override
	public String login(String username, String password) throws ExcepcionCredencialInvalida {
		// TODO Auto-generated method stub
		if ("admin".equals(username) && "admin".equals(password)) {
			
			this.sesion = Long.toString(System.currentTimeMillis());
			return this.sesion;
		}
		throw new ExcepcionCredencialInvalida(username);
	}

	
	//trabajo de logout
	@Override
	public void logout(String sesion) throws ExcepcionSesionNoValidaTiempoEjecucion {
		// TODO Auto-generated method stub
		chequearSesion(sesion);
		this.sesion = null;
	}

	@Override
	public boolean sesionEsValida(String sesion) {
		// TODO Auto-generated method stub
		return this.sesion != null && this.sesion.equals(sesion);
	}
	
	protected void chequearSesion(String sesion) throws ExcepcionSesionNoValidaTiempoEjecucion {
		
		if (!sesionEsValida(sesion)) {
			throw new ExcepcionSesionNoValidaTiempoEjecucion(sesion);
		}
	}
	
	//trabajo de obtener grupos

	@Override
	public Set<String> obtenerGrupos(String sesion)  {
		// TODO Auto-generated method stub
		Set<String> grupo = new HashSet<String>();
		
		grupo.add("string");
		
		return grupo;
		
	}

	@Override
	public void adicionarLibro(String sesion, String isbn, String titulo, String autor, String categoria) throws ExcepcionLibroInvalido {
		// TODO Auto-generated method stub
		LibroMutableImpl nuevo = new LibroMutableImpl(isbn); 
		nuevo.setTitulo(titulo);
		nuevo.setAutor(autor);
		nuevo.setCategoria(categoria);
		//Inventario.class.guardarLibro(nuevo);
		try {
			addBook(nuevo);
		} catch (ExcepcionLibroInvalido e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExcepcionSesionNoValidaTiempoEjecucion e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void addBook(LibroMutableImpl nuevo) throws ExcepcionLibroInvalido, ExcepcionSesionNoValidaTiempoEjecucion {
		Inventario inventario = buscarLibroEnInventario();
		inventario.guardarLibro(nuevo);
	}

	@Override
	public void modificarCategoriaLibro(String sesion, String isbn, String categoria) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removerLibro(String sesion, String isbn) throws ExcepcionLibroNoEncontrado {
		// TODO Auto-generated method stub
		try {
			deleteBook(isbn);
		} catch (ExcepcionLibroInvalido | ExcepcionSesionNoValidaTiempoEjecucion | ExcepcionLibroNoEncontrado e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void deleteBook(String isbn) throws ExcepcionLibroInvalido, ExcepcionSesionNoValidaTiempoEjecucion, ExcepcionLibroNoEncontrado {
		Inventario inventario = buscarLibroEnInventario();
		inventario.removerLibro(isbn);
	}

	@Override
	public Libro obtenerLibro(String sesion, String isbn) throws ExcepcionLibroNoEncontrado , ExcepcionSesionNoValidaTiempoEjecucion{
		// TODO Auto-generated method stub
		this.chequearSesion(sesion);
		Inventario inventario = buscarLibroEnInventario();
		return inventario.cargarLibro(isbn);
	}

	@Override
	public Set<String> buscarLibrosPorCategoria(String sesion, String categoriaLike) {
		// TODO Auto-generated method stub
		Set<String> inv = null;
		try {
			inv= getBook("Categoria", Inventario.CriterioBusqueda.CATEGORIA_LIKE);
		} catch (ExcepcionLibroInvalido | ExcepcionSesionNoValidaTiempoEjecucion | ExcepcionLibroNoEncontrado e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inv;
	}

	@Override
	public Set<String> buscarLibrosPorAutor(String session, String autorLike) {
		// TODO Auto-generated method stub
		Set<String> inv = null;
		try {
			inv= getBook("Autor", Inventario.CriterioBusqueda.AUTOR_LIKE);
		} catch (ExcepcionLibroInvalido | ExcepcionSesionNoValidaTiempoEjecucion | ExcepcionLibroNoEncontrado e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inv;
	}

	@Override
	public Set<String> buscarLibrosPorTitulo(String sesion, String tituloLike) {
		// TODO Auto-generated method stub
		Set<String> inv = null;
		try {
			inv= getBook("Titulo", Inventario.CriterioBusqueda.TITULO_LIKE);
		} catch (ExcepcionLibroInvalido | ExcepcionSesionNoValidaTiempoEjecucion | ExcepcionLibroNoEncontrado e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inv;
	}

	
	public Set<String> getBook(String criterio, CriterioBusqueda busqueda) throws ExcepcionLibroInvalido, ExcepcionSesionNoValidaTiempoEjecucion, ExcepcionLibroNoEncontrado {
		Inventario inventario = buscarLibroEnInventario();
		Map<CriterioBusqueda, String> critery = new HashMap<>();
		//CriterioBusqueda temp=Inventario.CriterioBusqueda.AUTOR_LIKE;
		//critery.put(Inventario.CriterioBusqueda.AUTOR_LIKE , criterio);
		critery.put(busqueda , criterio);
		Set<String> inv =inventario.buscarLibros(critery);
		return inv;
	}

	private Inventario buscarLibroEnInventario() throws ExcepcionSesionNoValidaTiempoEjecucion {
		
		String nombre = Inventario.class.getName();
		ServiceReference<?> ref = this.contexto.getServiceReference(nombre);
		if (ref == null) {
			throw new ExcepcionSesionNoValidaTiempoEjecucion(nombre);
		}
		return (Inventario) this.contexto.getService(ref);
 		
	}
	
}
