package com.grimstveit.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import helper.Const;
import objects.Ball;
import objects.Player;
import objects.PlayerAI;
import objects.Wall;

public class GameScreen extends ScreenAdapter {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private GameContactListener gameContactListener;

    private Player player;
    private PlayerAI playerAI;
    private Ball ball;
    private Wall wallTop, wallBottom;

    private TextureRegion[] numbers;

    public GameScreen(OrthographicCamera camera){
        this.camera = camera;
        this.camera.position.set(new Vector3(Pong.INSTANCE.getScreenWidth() / 2, Pong.INSTANCE.getScreenHeight() / 2, 0));
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0,0), false);
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.gameContactListener = new GameContactListener(this);
        this.world.setContactListener((this.gameContactListener));

        this.player = new Player(16,Pong.INSTANCE.getScreenHeight() / 2, this);
        this.playerAI = new PlayerAI(Pong.INSTANCE.getScreenWidth() - 16, Pong.INSTANCE.getScreenHeight() / 2, this);
        this.ball = new Ball(this);
        this.wallTop = new Wall(32, this);
        this.wallBottom = new Wall(Pong.INSTANCE.getScreenHeight() - 32, this);
        this.numbers = loadTextureSprite("numbers.png", 10);
    }

    public void update(){
        world.step(1/60f, 6, 2);

        this.camera.update();
        this.player.update();
        this.playerAI.update();
        this.ball.update();

        batch.setProjectionMatrix(camera.combined);

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
            this.ball.reset();
            this.player.setScore(0);
            this.playerAI.setScore(0);
        }

    }

    @Override
    public void render(float delta){
        update();
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();


        this.player.render(batch);
        this.playerAI.render(batch);
        this.ball.render(batch);
        this.wallTop.render(batch);
        this.wallBottom.render(batch);

        drawNumbers(batch, player.getScore(), 64, Pong.INSTANCE.getScreenHeight() - 55, 30 ,42);
        drawNumbers(batch, playerAI.getScore(), Pong.INSTANCE.getScreenWidth() - 96, Pong.INSTANCE.getScreenHeight() - 55, 30, 42);

        batch.end();

        this.box2DDebugRenderer.render(world, camera.combined.scl(Const.PPM));
    }

    private void drawNumbers(SpriteBatch batch, int number, float x, float y, float width, float height){
        if(number < 10){
            batch.draw(numbers[number], x, y, width, height);
        }else {
            batch.draw(numbers[Integer.parseInt((""+number).substring(0,1))], x, y, width, height);
            batch.draw(numbers[Integer.parseInt((""+number).substring(1,2))],x + 20, y, width, height);
        }
    }

    private TextureRegion[] loadTextureSprite(String filename, int columns){
        Texture texture = new Texture(filename);
        return TextureRegion.split(texture, texture.getWidth() / columns, texture.getHeight())[0];
    }

    public World getWorld() {
        return world;
    }

    public Ball getBall() {
        return ball;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerAI getPlayerAI() {
        return playerAI;
    }
}
