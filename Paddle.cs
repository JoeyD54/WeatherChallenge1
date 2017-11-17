using UnityEngine;
using System.Collections;

public class Paddle : MonoBehaviour {

    float mousePosInBlocks;
    Vector2 paddlePos = new Vector2 (8f, .5f);
	public bool autoPlay = false;
	private Ball ball;

	// Use this for initialization
	void Start () {
		ball = FindObjectOfType<Ball>();
	}
	
	// Update is called once per frame
	void Update () {
		if(!autoPlay)
			moveWithMouse();
		else
			PaddleBot();
	}

	void PaddleBot() {
		paddlePos = new Vector2 (0.5f, this.transform.position.y);
		Vector2 ballPos = ball.transform.position;
		paddlePos.x = Mathf.Clamp(ballPos.x, .5f, 15.5f);

		transform.position = paddlePos;
	}

	void moveWithMouse() {
		mousePosInBlocks = Input.mousePosition.x / Screen.width * 16;

		paddlePos.x = Mathf.Clamp(mousePosInBlocks, .5f, 15.5f);

		transform.position = paddlePos;
	}
}
