package renderer.suntabu.com.dimondprogressbar;

/**
 * Created by gouzhun on 2016/5/9.
 */
public class Vector2 {
    public float x, y;
    private boolean isNormalized;

    public Vector2() {
        x = y = 0;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }


    public Vector2 normalize() {
        //TODO:
        float length = (float) Math.sqrt(x * x + y * y);
        float x1 = 0, y1 = 0;
        if (length != 0) {
            x1 = this.x / length;
            y1 = this.y / length;

        }

        return new Vector2(x1, y1);
    }


    public Vector2 plus(float x, float y) {
        float x1 = this.x + x;
        float y1 = this.y + y;
        return new Vector2(x1, y1);
    }

    public Vector2 plus(Vector2 other) {
        return plus(other.x, other.y);
    }


    public Vector2 minus(float x, float y) {
        float x1 = this.x - x;
        float y1 = this.y - y;
        return new Vector2(x1, y1);
    }

    public Vector2 minus(Vector2 other) {
        return minus(other.x, other.y);
    }


    public float dot(float x, float y) {
        //TODO:
        return 0;
    }

    public float dot(Vector2 other) {
        return dot(other.x, other.y);
    }


    public Vector2 scale(float scale) {
        float x1 = this.x * scale;
        float y1 = this.y * scale;
        return new Vector2(x1, y1);
    }
}
