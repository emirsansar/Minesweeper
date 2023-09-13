import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Minesweeper implements MouseListener {
	private JFrame frame;
	private final static int ROW = 10;
	private final static int COLUMN = 10;
	private final static int TOTAL_MINES = 10;
	private Btn[][] board = new Btn[ROW][COLUMN];
	private int openedBtn = 0;
	private int seconds = 0;
	private boolean isGameOver = false;

	private JPanel panelControl, panelGame;
	private JLabel minesLabel, timeLabel, mineIcon, timeIcon;
	private JButton btnRestart, btnExit;
	private ImageIcon appIcon;
	private Timer timer;
	private Color colorFrame, colorBtn;

    public Minesweeper() {
        frame = new JFrame("Mineseeper - github.com/emirsansar");
        frame.setSize(675,760);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        colorFrame = new Color(200, 200, 200);
        frame.getContentPane().setBackground(colorFrame);
        frame.setLayout(null);
        appIcon = new ImageIcon(Minesweeper.class.getResource("/images/appIcon.png"));
	    frame.setIconImage(appIcon.getImage());

        panelControl = new JPanel(null); 
        panelControl.setBounds(5, 3, 650, 50);
        panelControl.setBackground(colorFrame);
        panelControl.setBorder(new LineBorder(Color.BLACK, 1));
        
        panelGame = new JPanel(new GridLayout(10, 10)); 
        panelGame.setBounds(5, 60, 650, 650);
        panelGame.setBackground(colorFrame);
        panelGame.setBorder(new LineBorder(Color.BLACK, 1));
        
        mineIcon = new JLabel();
        mineIcon.setIcon( new ImageIcon(Minesweeper.class.getResource("/images/panelMine.png")) );
        mineIcon.setBounds(5,10,30,30);
        panelControl.add(mineIcon);
        
        minesLabel = new JLabel("Number of Mines: 10");
        minesLabel.setBounds(37, 0, 185, 50);
        panelControl.add(minesLabel);
        
        timeIcon = new JLabel();
        timeIcon.setIcon( new ImageIcon(Minesweeper.class.getResource("/images/panelTime.png")) );
        timeIcon.setBounds(230,10,30,30);
        panelControl.add(timeIcon);
        
        timeLabel = new JLabel("Elapsed Time: 0:00");
        timeLabel.setBounds(260, 0, 170, 50);
        panelControl.add(timeLabel);
        
        Font labelFont = new Font("Tahoma", Font.BOLD, 17);
        
        colorBtn = new Color(225, 225, 225);
        
        btnRestart = new JButton("Restart");
        btnRestart.setBounds(450, 7, 100, 35);
        btnRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        btnRestart.setBackground(colorBtn);
        btnRestart.setFont(labelFont);
        panelControl.add(btnRestart);
        
        btnExit = new JButton("Exit");
        btnExit.setBounds(560, 7, 80, 35);
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               	System.exit(0);
            }
        });
        btnExit.setBackground(colorBtn);
        panelControl.add(btnExit);
        
        minesLabel.setFont(labelFont);
        timeLabel.setFont(labelFont);
        btnRestart.setFont(labelFont);
        btnExit.setFont(labelFont);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                seconds++;
                int minutes = seconds / 60;
                int remainingSeconds = seconds % 60;
                timeLabel.setText( String.format("Elapsed Time: %d:%02d", minutes, remainingSeconds) );
            }
        }, 0, 1000);
            
        for(int row=0; row < ROW; row++) {
            for(int col=0; col < COLUMN; col++) {
                Btn btn = new Btn(row, col);
                panelGame.add(btn);
                btn.addMouseListener(this);
                board[row][col] = btn;
            }
        }

        generateMine();
        updateCount();

        frame.add(panelControl);
        frame.add(panelGame);
        frame.setVisible(true);
    }
	

	//Methods
	public void generateMine() {
        int i=0;
        
        while (i != TOTAL_MINES) {
    		int randRow = (int) (Math.random() * ROW);
            int randCol = (int) (Math.random() * COLUMN);
            if( board[randRow][randCol].isMine() == false ) {
                board[randRow][randCol].setMine(true);
                i++;
            }
        }
	}
	
	public void print() {
		for(int row=0; row < ROW; row++) {
			for(int col=0; col < COLUMN; col++) {
				Btn currentBtn = board[row][col];
				
				if( currentBtn.isMine() ) {
					currentBtn.setIcon( new ImageIcon(Minesweeper.class.getResource("/images/mine.png")) );
				} else {
					if( currentBtn.getCount() > 0 && !currentBtn.getText().equals("0") && !currentBtn.isFlag()) {
						currentBtn.setText( Integer.toString( currentBtn.getCount()) );
					}		
				}
			}
		}
	}
	
	public void updateCount() {
		for(int row=0; row < ROW; row++) {
			for(int col=0; col < COLUMN; col++) {
				if ( board[row][col].isMine() )   // If the area is mined, nearby areas are checked and the 'count' variable is updated by using the checkArea() method.
					checkArea(row, col);
			}
		}
	}
	
	public void checkArea(int row, int col) {
		int newRow, newCol;
		final int[] dx = {0, 0, -1, +1, -1, +1, +1, -1};  // x coordinates
        final int[] dy = {1, -1, 0, 0, -1, +1, -1, +1};   // y coordinates
        
        for(int i=0; i<8; i++){     // In this loop, it controls the four cells adjacent to board[row][col], respectively.
            newRow = row + dx[i];
            newCol = col + dy[i];
            
            if( isValidCoordinate(newRow, newCol) ) {  // Checks if array bounds are exceeded.
            	board[newRow][newCol].setCount( board[newRow][newCol].getCount() + 1 );
            }
        }    
	}
        
    public boolean isValidCoordinate(int row, int col){
        return (row >= 0 && row < ROW) && (col >= 0 && col < COLUMN);
    }
    
    public void openArea(int r, int c) {   // If player click on the mine-free area, this method will open the surrounding mine-free areas by working recursively.
    	Btn currentBtn = board[r][c];
    	
    	if ( !isValidCoordinate(r,c) || currentBtn.getText().length() > 0 || !currentBtn.isEnabled() )
    		return;
    	
    	else if ( currentBtn.getCount() != 0 && !currentBtn.isFlag() ) {
    		currentBtn.setText( Integer.toString(currentBtn.getCount()) );
    		currentBtn.setEnabled(false);
    		openedBtn++;
    	} 
    	else {
    		currentBtn.setEnabled(false);
    		openedBtn++;
    		
    		int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
    		
    		for (int[] dir : directions) {
                int newRow = r + dir[0];
                int newCol = c + dir[1];

                if (isValidCoordinate(newRow, newCol)) {
                    openArea(newRow, newCol);
                }
            }
    	}
    }
    
    public boolean checkWin() {
        return openedBtn == (ROW * COLUMN) - TOTAL_MINES;
    }
    
    public void restartGame() {
        isGameOver = false;
        openedBtn = 0;
        seconds = 0;
        timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                seconds++;
                int minutes = seconds / 60;
                int remainingSeconds = seconds % 60;
                timeLabel.setText(String.format("Elapsed Time: %d:%02d", minutes, remainingSeconds));
            }
        }, 0, 1000);

        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COLUMN; col++) {
                Btn btn = board[row][col];
                btn.setCount(0);
                btn.setMine(false);
                btn.setEnabled(true);
                btn.setIcon(null);
                btn.setText("");
                btn.setFlag(false);
            }
        }
        generateMine();
        updateCount();
    }
    
    public void gameOver() {
    	timer.cancel();
    	JOptionPane.showMessageDialog(frame, "Game Over! You stepped on a mine!");
    	isGameOver = true;
    }
    
    public void gameWon() {
		timer.cancel();
		JOptionPane.showMessageDialog(frame, "Congratulations, you won the game!");
    }
    
    public void toggleFlag(Btn btn) {
		if ( !btn.isFlag() ) {
			btn.setIcon( new ImageIcon(Minesweeper.class.getResource("/images/flag.png")) );
			btn.setFlag(true);
		} else {
			btn.setIcon(null);
			btn.setFlag(false);
		}
    }
    
	@Override
	public void mouseClicked(MouseEvent e) {
		Btn clickedBtn = (Btn) e.getComponent();
		
		if ( !isGameOver ) {
			
			if ( e.getButton() == MouseEvent.BUTTON1 ) {
				if ( clickedBtn.isMine() ) {
					gameOver();
					print();
				} else {
					openArea( clickedBtn.getRow(), clickedBtn.getCol() );
					if ( checkWin() ) {
						gameWon();
						print();
					}
				}
			} 
			else if (e.getButton() == MouseEvent.BUTTON3 ) {
				toggleFlag(clickedBtn);
			}
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}
