package es.ull.etsii.eanor.prolog;

public class Attribute {
	String nombre;
	
	public Attribute(String pnombre) {
		nombre = pnombre.toLowerCase();
	}
	
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Attribute)
			return ((Attribute) obj).nombre.toLowerCase().equals(this.nombre.toLowerCase());
		return false;
	}
	
	public String toString() {
		return nombre;
	}
}
