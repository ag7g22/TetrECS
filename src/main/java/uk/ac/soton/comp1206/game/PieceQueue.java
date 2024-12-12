package uk.ac.soton.comp1206.game;

/**
 * The PieceQueue Class stores the received GamePieces from the server and passes to the
 * MultiplayerGame Class
 */
public class PieceQueue {

  //Queue Class used to hold the server's pieces (Taken from the labs)
  /**
   * Capacity of the queue
   */
  protected final int size;

  /**
   * The int List representing the queue
   */
  protected final int[] queue;

  /**
   * Pointer at start of queue
   */
  protected int head = 0;

  /**
   * Pointer at end of queue
   */
  protected int tail = 1;

  /**
   * Create a new PieceQueue with appropriate capacity
   *
   * @param capacity capacity
   */
  public PieceQueue(int capacity) {
    this.size = capacity;
    queue = new int[capacity];
    // -1 is empty slot placeholder
    for (int i = 0; i < capacity; i++) {
      queue[i] = -2;
    }
  }

  /**
   * Enqueues a new Piece value to the queue
   *
   * @param i Piece Value
   * @throws IndexOutOfBoundsException if Queue is full
   */
  public void enqueue(int i) throws IndexOutOfBoundsException {
    //Empty queue case
    if (isEmpty()) {
      queue[head] = i;
      //Full queue case
    } else if (head == tail) {
      throw new IndexOutOfBoundsException("Queue is full!");
      //Partially full queue case
    } else if (queue[tail] == -2) {
      queue[tail] = i;
      if (tail == size - 1) {
        tail = 0;
      } else {
        tail++;
      }
    }
  }

  /**
   * Dequeues the last Piece at the end of the PieceQueue
   *
   * @return the next piece to be played
   * @throws IndexOutOfBoundsException if Queue is empty
   */
  public int dequeue() throws IndexOutOfBoundsException {
    int dqNum;
    if (isEmpty()) {
      throw new IndexOutOfBoundsException("Queue is empty!");
    } else {
      dqNum = queue[head];
      queue[head] = -2;
      //cases whether to circle around array or not.
      if (head == size - 1) {
        head = 0;
      } else {
        head++;
        if (head == tail) {
          tail++;
        }
      }
    }
    return dqNum;
  }

  /**
   * Checks if the queue is empty or not
   *
   * @return whether queue is empty
   */
  public boolean isEmpty() {
    boolean empty = true;
    for (int num : queue) {
      if (num != -2) {
        empty = false;
        break;
      }
    }
    return empty;
  }
}
