package basicchain;

import java.util.Random;
import java.util.Scanner;

/**
 * Simple Smartcontract
 * @author kaito
 * 2 players predict if it would rain tomorow
 * Each player send 10 coin as đặt cọc money
 * After 60s, the result will be generated randomly (rain or not rain)
 * Winner will claim all the money 
 */
public class SmartContract {

	private Wallet tmpWallet;
	private int res;
	private Wallet userA, userB;
	private int predictA, predictB;
	private final float betMoney = 10.0f;
	private Transaction transaction;
	
	/**
	 * Create new contract with 2 player
	 */
	public SmartContract() {
	}
	
        /*
        Start smartcontract
        */
        public void start() {
            setUsers();
            execute();
        }
        
        private void setUsers() {
            Wallet curWallet = null;
            
            while (curWallet == null) {
                Scanner inp = new Scanner(System.in);
		System.out.println("Username player 1: ");
		String username = inp.nextLine();
		System.out.println("Password player 1: ");
		String password = inp.nextLine();
		curWallet = Wallet.checkUser(username, password);
		if (curWallet == null) {
			System.out.println("Login Fail!!!");
		}else {
			userA = curWallet;
                        System.out.println("Will it rain tomorow? (1 = Yes, 0 = No)");
                        int ans = inp.nextInt();
                        predictA = ans % 2;
		}
            }
            
            curWallet = null;
            while (curWallet == null) {
                Scanner inp = new Scanner(System.in);
		System.out.println("Username player 2: ");
		String username = inp.nextLine();
		System.out.println("Password player 2: ");
		String password = inp.nextLine();
		curWallet = Wallet.checkUser(username, password);
		if (curWallet == null) {
			System.out.println("Login Fail!!!");
		}else {
			userB = curWallet;
                        System.out.println("Will it rain tomorow? (1 = Yes, 0 = No)");
                        int ans = inp.nextInt();
                        predictB = ans % 2;
		}
            }
        }
        
	public void execute() {
		tmpWallet = new Wallet();
		//tmpWallet.publicKey;
		transaction = userA.sendFunds(tmpWallet.publicKey, betMoney);
		BasicCoin.currBlock.addTransaction(transaction);
		
		transaction = userB.sendFunds(tmpWallet.publicKey, betMoney);
		BasicCoin.currBlock.addTransaction(transaction);
		checkResult();
	}
	
	private void checkResult() {
		final Thread checkResult;
            checkResult = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(30000);
                        Random r = new Random();
                        int rain = r.nextInt((int)1e9)%2;
                        if (rain == 1) {
                            System.out.println("It will rain tomorow!");
                        }
                        else {
                            System.out.println("It won't rain tomorow!");
                        }
                        
                        if (predictA == predictB) {
                            System.out.println("Both users have same answer.");
                            transaction = tmpWallet.sendFunds(userA.publicKey, betMoney);
                            BasicCoin.currBlock.addTransaction(transaction);
                            
                            transaction = tmpWallet.sendFunds(userB.publicKey, betMoney);
                            BasicCoin.currBlock.addTransaction(transaction);
                        } else {
                            if (rain == predictA) {
                                System.out.println("A is the winner.");
                                transaction = tmpWallet.sendFunds(userA.publicKey, betMoney*2);
                                BasicCoin.currBlock.addTransaction(transaction);
                            } else {
                                System.out.println("B is the winner.");
                                transaction = tmpWallet.sendFunds(userB.publicKey, betMoney*2);
                                BasicCoin.currBlock.addTransaction(transaction);
                            }
                        }
                        
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
        checkResult.start();
	}
}
