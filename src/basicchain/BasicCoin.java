package basicchain;

import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
//import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;
import static org.bouncycastle.crypto.tls.CipherType.block;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class BasicCoin {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	
	public static int difficulty = 3;
	public static float minimumTransaction = 0.1f;
	public static Wallet walletA;
	public static Wallet walletB;
	public static Transaction genesisTransaction;
	public static Block currBlock;
	
	public static ArrayList<Wallet> listWallet = new ArrayList<>();
	
	public static void main(String[] args) {	
                Gui gui = new Gui();
                gui.setVisible(true);
                //add our blocks to the blockchain ArrayList:
		Security.addProvider(new BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider
		
		//Create wallets:
		walletA = new Wallet("admin1", "admin");
		walletB = new Wallet("admin2", "admin");		
		Wallet coinbase = new Wallet();
		listWallet.add(walletA);
		listWallet.add(walletB);
		
		//create genesis transaction, which sends 100 NoobCoin to walletA: 
		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 10000f, null);
		genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction	
		genesisTransaction.transactionId = "0"; //manually set the transaction id
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
		
                
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
                
		addBlock(genesis);
		
		currBlock = new Block(genesis.hash);
		currBlock.addTransaction(listWallet.get(0).sendFunds(listWallet.get(1).publicKey, 5000f));
		final Thread createBlock = new Thread() {
             @Override
             public void run() {
             	while(true) {
             		
             		try {
						sleep(5000);
						addBlock(currBlock);
                                                gui.getBlockChain().setText(gui.getBlockChain().getText() + StringUtil.getJson(currBlock));
						currBlock = new Block(currBlock.hash);
                                                   

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
             	}
             }
         };
         createBlock.start();
		//testing
		//Block block1 = new Block(genesis.hash);
		
		
		outer: while(true) {
			System.out.println("1: Smart Contract");
			System.out.println("2: Get Balance");
			System.out.println("3: Send Money");
			System.out.println("4: Show Blocks");
			System.out.println("5: Create Walllet");
			System.out.println("6: Exit!!!");
			Scanner inp = new Scanner(System.in);
			int selection = inp.nextInt();
			switch(selection) {		
				case 1: {
                                    SmartContract sc = new SmartContract();
                                    sc.start();
                                }; break;
				
				case 2: getBalance();
					break;
				case 3: sendMoney();
					break;
				case 4: showBlock();
					break;
				case 5: createWallet();
					break;
				case 6: break outer;						
			}
		}
		
		isChainValid();
		
	}
	
	static void showBlock() {
		for (Block block : blockchain) {
			System.out.println(StringUtil.getJson(block)); 
		}
	}
	
	static void getBalance() {
		Scanner inp = new Scanner(System.in);
		System.out.println("Username: ");
		String username = inp.nextLine();
		System.out.println("Password: ");
		String password = inp.nextLine();
		Wallet curWallet = Wallet.checkUser(username, password);
		if(curWallet == null) {
			System.out.println("Login Fail!!!");
		}else {
			System.out.println("Your Balance : " + curWallet.getBalance());
		}
	}
	
	static void sendMoney() {
		Wallet curWallet = new Wallet();
		Wallet repWallet = new Wallet();
;		Scanner inp = new Scanner(System.in);
		System.out.println("Username: ");
		String username = inp.nextLine();
		System.out.println("Password: ");
		String password = inp.nextLine();
		curWallet = Wallet.checkUser(username, password);
		if(curWallet == null) {
			System.out.println("Login Fail!!!");
		}else {
			System.out.println("Your Balance : " + curWallet.getBalance());
			System.out.println();
			System.out.println("Receipter Userame : ");
			String repUsername = inp.nextLine();
			PublicKey publicKey = Wallet.checkRep(repUsername);
			
			System.out.println("Money : ");
			float value = inp.nextFloat();
			
			currBlock.addTransaction(curWallet.sendFunds(publicKey, value));
		}
	}
	
	static void createWallet() {
		Scanner inp = new Scanner(System.in);
		System.out.println("Username: ");
		String username = inp.nextLine();
		System.out.println("Password: ");
		String password = inp.nextLine();
		Wallet wallet = new Wallet(username, password);
		listWallet.add(wallet);
		System.out.println("Success !!!");
	}
	
	public void addBlock() {
		//testing
//				Block block1 = new Block(genesis.hash);
//				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//				System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
//				block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
//				addBlock(block1);
//				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//				System.out.println("WalletB's balance is: " + walletB.getBalance());
//				
//				Block block2 = new Block(block1.hash);
//				System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
//				block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
//				addBlock(block2);
//				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//				System.out.println("WalletB's balance is: " + walletB.getBalance());
//				
//				Block block3 = new Block(block2.hash);
//				System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
//				block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
//				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//				System.out.println("WalletB's balance is: " + walletB.getBalance());
	}
	
	
	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		//loop through blockchain to check hashes:
		for(int i=1; i < blockchain.size(); i++) {
			
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("#Current Hashes not equal");
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("#Previous Hashes not equal");
				return false;
			}
			//check if hash is solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}
			
			//loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);
				
				if(!currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false; 
				}
				
				for(TransactionInput input: currentTransaction.inputs) {	
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					if(tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}
					
					if(input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}
					
					tempUTXOs.remove(input.transactionOutputId);
				}
				
				for(TransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}
				
				if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
				
			}
			
		}
		System.out.println("Blockchain is valid");
		return true;
	}
	
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
}

/*
 * public static void main(String[] args) {	
		//add our blocks to the blockchain ArrayList:
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider
		
		//walletA = new Wallet();
		//walletB = new Wallet();
		
		//System.out.println("Private and public keys:");
		//System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
		//System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
		
		createGenesis();
		
		//Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5);
		//transaction.signature = transaction.generateSignature(walletA.privateKey);
		
		//System.out.println("Is signature verified:");
		//System.out.println(transaction.verifiySignature());
		
	}
 */

//System.out.println("Trying to Mine block 1... ");
//addBlock(new Block("Hi im the first block", "0"));