package tetris;

public class CheckingEngine extends Thread {
	private Object semaphore;
	private boolean checkingMatrix = false, cannotMove = false;
	private char[][] blocksMatrix;

	public CheckingEngine(Object semaphore) {
		this.semaphore = semaphore;
	}

	@Override
	public void run() {
		while (true) {
			synchronized (semaphore) {
				try {
					if (!checkingMatrix)
						semaphore.wait();
					checkMatrix();
				} catch (Throwable error) {
					error.printStackTrace();
				}
				checkingMatrix = false;
				semaphore.notifyAll();
			}
		}
	}

	private void checkMatrix() {
		//System.out.println("Checking");
		for (int i = 14; i > -1; i--)
			for (int j = 0; j < 10; j++) {
				if (blocksMatrix[i][j] == '1' || blocksMatrix[i][j] == '2'
						|| blocksMatrix[i][j] == '3'
						|| blocksMatrix[i][j] == '4'
						|| blocksMatrix[i][j] == '5'
						|| blocksMatrix[i][j] == '6'
						|| blocksMatrix[i][j] == '7') {
					if (i == 14) {
						cannotMove = true;
						return;
					} else {

						if (blocksMatrix[i + 1][j] == '~') {
							cannotMove = true;
							return;
						}
					}
				}
			}
		cannotMove = false;
		return;
	}

	public void setMatrix(char[][] matrix) {
		blocksMatrix = matrix;
	}

	public void startChecking() {
		checkingMatrix = true;
	}

	public boolean getChecking() {
		return checkingMatrix;
	}

	public boolean getMoveState() {
		return cannotMove;
	}
}
