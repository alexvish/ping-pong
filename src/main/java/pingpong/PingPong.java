package pingpong;

public class PingPong {

	private static final long CYCLES = 1000L * 1000L;
	
	private Object obj = new Object();
	private boolean ping = false;
    
	public static void main(String[] args) throws Exception {
		PingPong pingPong = new PingPong();
		pingPong.doRun();
	}

	public void doRun() throws Exception {
		Thread pingThread = new Thread(new PingRunner(), "ping thread");
		Thread pongThread = new Thread(new PongRunner(), "pong thread");

		long start = System.nanoTime();

		pingThread.start();
		pongThread.start();

		pongThread.join();

		long durationNanos = System.nanoTime() - start;
		System.out.printf("Duration : %,d (microseconds)\n",
				durationNanos / 1000L);
		System.out.printf("Average ping: %,d microseconds/op\n", durationNanos
				/ (1000L * 2 * CYCLES));

	}

	private class PingRunner implements Runnable {

		public void run() {
			try {
				int i = 0;
				while (i < CYCLES) {
					synchronized (obj) {
						if (!ping) {
							ping = true;
							i++;
							obj.notify();
							obj.notifyAll();
						} else {
							obj.wait();
						}						
					}
				}
			} catch (InterruptedException e) {
				// log.error
			}
		}
	}

	private class PongRunner implements Runnable {

		public void run() {
			try {
				int i = 0;
				while (i < CYCLES) {
					synchronized (obj) {
						if (ping) {
							ping = false;
							i++;
							obj.notify();
							obj.notifyAll();
						} else {
							obj.wait();
						}
					}
				}
			} catch (InterruptedException e) {
				// log.error
			}
		}
	}

}
