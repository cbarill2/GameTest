package game;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;

import game.framework.Animation;
import game.framework.IRenderable;

//import java.util.List;
//import java.util.Map;

public abstract class Unit implements IRenderable {
	
	private final float MOVE_SPEED = 0.014f;
	private String name;
	protected Vector2f position = new Vector2f(0f, 0f);
	protected Vector2f speed = new Vector2f(0f, 0f); 
	private int currentHealth, maxHealth;
	private int baseAttack, totalAttack;
	private int baseDefense, totalDefense;
	private boolean alive;
	private int level;
	protected DungeonRoom location;
	private boolean movingRight = false;
	private boolean movingLeft = false;
	private boolean movingUp = false;
	private boolean movingDown = false;
	protected Animation anim;
	protected int vao, vbo, ebo;
	protected float[] vertices;
	
	protected ArrayList<Item> inventory = new ArrayList<Item>();
//	private Map<String, Item> equipment;	//TODO: implement this
	
	public Unit(DungeonRoom location, String name, int level, int bonusHealth){
		this.location = location;
		this.name = name;
		this.level = level;
		
		this.maxHealth = 10 + level/2 + bonusHealth;
		this.currentHealth = this.maxHealth;
		this.alive = true;
		this.baseAttack = 10 + level/3 + this.maxHealth/4;
		this.baseDefense = 10 + level/3 + this.baseAttack/4;
		this.totalAttack = this.baseAttack;
		this.totalDefense = this.baseDefense;
		this.anim = new Animation();
	}

	public int takeDamage(int incomingAttackPower){
		int damage = incomingAttackPower - this.totalDefense;
		damage = (damage < 1) ? 0 : damage;
		this.currentHealth -= damage;
		this.alive = (this.currentHealth > 0) ? true : false;
		return damage;
	}
	
	protected void gainLevel(){
		System.out.printf("%s has grown stronger! Level increased to %d\n", this.name,this.level);
		this.level++;
		this.maxHealth += this.level%2;
		this.currentHealth = this.maxHealth;
		this.baseAttack++;
		this.baseDefense++;
	}
	
	public int getTotalAttack(){
		return this.totalAttack;
	}
	
	public boolean isAlive(){
		return this.alive;
	}
	
	public int getLevel() {
		return level;
	}

	public String getName() {
		return this.name;
	}
	
	public DungeonRoom getLocation(){
		return this.location;
	}
	
	protected void setLocation(DungeonRoom room){
		this.location = room;
	}

	public float getSpeedX() {
		return speed.x;
	}

	public float getSpeedY() {
		return speed.y;
	}
	
	public void moveRight() {
		speed.x = MOVE_SPEED;
		this.movingRight = true; 
	}

	public void moveLeft() {
		speed.x = -MOVE_SPEED;
		this.movingLeft = true;
	}

	public void moveUp() {
		speed.y = MOVE_SPEED;
		this.movingUp = true;
	}

	public void moveDown() {
		speed.y = -MOVE_SPEED;
		this.movingDown = true;
	}

	public void stopRight() {
		setMovingRight(false);
		stop();
	}

	public void stopLeft() {
		setMovingLeft(false);
		stop();
	}

	public void stopUp() {
		setMovingUp(false);
		stop();
	}

	public void stopDown() {
		setMovingDown(false);
		stop();
	}
	
	public void stopAll() {
		setMovingUp(false);
		setMovingDown(false);
		setMovingRight(false);
		setMovingLeft(false);
		stop();
	}

	private void stop() {
		if (!isMovingRight() && !isMovingLeft()) {
			speed.x = 0;
		}

		if (!isMovingRight() && isMovingLeft()) {
			moveLeft();
		}

		if (isMovingRight() && !isMovingLeft()) {
			moveRight();
		}
		
		if (!isMovingUp() && !isMovingDown()) {
			speed.y = 0;
		}

		if (!isMovingUp() && isMovingDown()) {
			moveDown();
		}

		if (isMovingUp() && !isMovingDown()) {
			moveUp();
		}

	}
	
	public boolean isMovingRight() {
		return movingRight;
	}

	private void setMovingRight(boolean movingRight) {
		this.movingRight = movingRight;
	}

	public boolean isMovingLeft() {
		return movingLeft;
	}

	private void setMovingLeft(boolean movingLeft) {
		this.movingLeft = movingLeft;
	}
	
	public boolean isMovingUp() {
		return movingUp;
	}

	private void setMovingUp(boolean movingUp) {
		this.movingUp = movingUp;
	}

	public boolean isMovingDown() {
		return movingDown;
	}

	private void setMovingDown(boolean movingDown) {
		this.movingDown = movingDown;
	}

	public int getVao() {
		return vao;
	}
	public int getVbo() {
		return vbo;
	}
	public int getEbo() {
		return ebo;
	}
	public int getTexture() {
		return anim.getFrameTexture();
	}

	public abstract float[] init(int vao);
	
	protected void update() {
		anim.update(10);
		
		vertices[0] = position.x - 0.13f;
		vertices[8] = position.x - 0.13f;
		vertices[16] = position.x + 0.13f;
		vertices[24] = position.x + 0.13f;
		vertices[1] = position.y - 0.16f;
		vertices[9] = position.y + 0.16f;
		vertices[17] = position.y - 0.16f;
		vertices[25] = position.y + 0.16f;
		vertices[26] = 0.8f;
		vertices[10] = 	0.8f;
		
		FloatBuffer vBuffer = MemoryUtil.memAllocFloat(vertices.length);
		vBuffer.put(vertices).flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
	    glBufferData(GL_ARRAY_BUFFER, vBuffer, GL_STATIC_DRAW);
	    MemoryUtil.memFree(vBuffer);
	    
	    glBindBuffer(GL_ARRAY_BUFFER, 0);
	    glBindVertexArray(0);
	    
	}
	
}
