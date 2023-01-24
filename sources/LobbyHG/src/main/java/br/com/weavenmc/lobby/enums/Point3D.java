package br.com.weavenmc.lobby.enums;

public class Point3D {
	
	public float x;
	public float y;
	public float z;

	public Point3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point3D rotate(float rot) {
		double cos = Math.cos(rot);
		double sin = Math.sin(rot);

		return new Point3D((float) (x * cos + z * sin), y, (float) (x * -sin + z * cos));
	}
}