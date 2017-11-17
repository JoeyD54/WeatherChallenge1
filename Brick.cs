using UnityEngine;
using System.Collections;

public class Brick : MonoBehaviour {

	public GameObject smoke;
	public Sprite[] hitSprites;
	public AudioClip punch;
	public AudioClip brickBreak;
	private int hitCount;
	private LevelManager levelManager;
	public static int breakableCount = 0;
	private bool isBreakable;

	// Use this for initialization
	void Start () {
		isBreakable = (this.tag == "Breakable");
		//Keep track of breakable bricks
		if (isBreakable)
			breakableCount++;
		hitCount = 0;
		levelManager = FindObjectOfType<LevelManager>();
	}
	
	// Update is called once per frame
	void Update () {
	
	}

	private void OnCollisionEnter2D(Collision2D col) {
		if(hitCount < hitSprites.Length + 1)
			AudioSource.PlayClipAtPoint(punch, transform.position);
		else
			AudioSource.PlayClipAtPoint(brickBreak, transform.position);
		if(isBreakable)
			HitHandler();
	}

	void SmokeStart() {
		GameObject smokePuff = Instantiate(smoke, transform.position, Quaternion.identity) as GameObject;
		smokePuff.GetComponent<ParticleSystem>().startColor = gameObject.GetComponent<SpriteRenderer>().color;
	}

	void HitHandler() {
		hitCount++;
		int maxHits = hitSprites.Length + 1;
		if (hitCount >= maxHits){
			breakableCount--;
			levelManager.BrickDestroyed();
			SmokeStart();
			Destroy(gameObject);
		}
		else
			LoadSprites();
	}

	void LoadSprites() {
		int spriteIndex = hitCount - 1;
		if(hitSprites[spriteIndex])
			GetComponent<SpriteRenderer>().sprite = hitSprites[spriteIndex];
		else
			Debug.LogError("Brick sprite missing");
	}
}
