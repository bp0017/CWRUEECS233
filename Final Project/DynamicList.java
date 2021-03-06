/** Ben Pierce (bgp12)
** For this  project, I decided to focus on time-efficency. This is NOT intended
** to be a memory-efficent implementation; which would be quite possible, but diffrent. 
** Instead, I focus getting the best BigO complexity possible.
**/
import java.util.*;
public class DynamicList<T extends Comparable<T>>{
	private static final int HASH_THRESHOLD = 700; //number that we switch to a hash table, regardless of other factors
	private static int SIZE;
	private BasicArrayList<T> arr;
	private BasicLinkedList<T> linked;
	private BasicHashTable<T> hash;

	private static int addInOrder = 0; //how many times each of these operations have been performed
	private static int removeLargest = 0;
	private static int search = 0;


	private int numElements;
	private int state;

	public DynamicList(int size){ 
		this.SIZE = size;
		this.numElements = 0;
		this.arr = new BasicArrayList(SIZE);
		this.linked = new BasicLinkedList();
		this.hash = new BasicHashTable(500000); //TODO: HASH TABLE SIZE ADAPT
		this.state = 0;

	}

	//0 = arraylist, 1 = linkedlist, 2 = hash tabe
	public void setState(int stateKey){
		if(this.state == 0 && stateKey ==1 && !compare(0)){ //from arraylist to linkedList WORKS
			BasicLinkedList<T> tmp = new BasicLinkedList();
			if(arr.size() !=0 && numElements != 0){
				for(int i = 0; i < arr.size(); i++){
					if(arr.get(i) != null)
						tmp.addInOrder((T)arr.get(i));
				}
			}
			linked = tmp;
		}
		else if(this.state == 0 && stateKey == 2 && !compare(2)){ //from arraylist to hash
			BasicHashTable<T> tmp = new BasicHashTable(numElements); //TODO: WRITE HASHTABLE ENSURE CAPACITY METHOD
			for(int i = 0; i < numElements; i++){
				tmp.put(arr.get(i),arr.get(i));
			}
			hash = tmp;
		}
		else if(this.state == 1 && stateKey ==0 && !compare(0)){ //from linked to array
			BasicArrayList<T> tmp = new BasicArrayList(numElements+1);
			LinkedNode cursor = linked.getHead();
			while(cursor != null){
				tmp.addInOrder((T)cursor.getData());
				cursor = cursor.getLink();
			}
			arr = tmp;
		}
		else if(this.state == 1 && stateKey == 2 && !compare(1)){ //from linkedlist to hashtable
			LinkedNode cursor = linked.getHead();
			BasicHashTable<T> tmp = new BasicHashTable(numElements+1);
			int i = 0;
			while(cursor != null){
				tmp.put((T)cursor.getData(),(T)cursor.getData());
				cursor = cursor.getLink();
			}
			hash = tmp;
		}
		else if(this.state == 2 && stateKey==0 &&  !compare(2)){ //from hash to array
			BasicArrayList<T> tmp = new BasicArrayList(numElements+1);
			Object[] items = hash.items();
			for(Object x: items){
				System.out.print((T)x + " ");
			}
			for(int i = 0; i < items.length; i++){
				tmp.addInOrder((T)items[i]);
			}
			arr = tmp;
		}
		else if(this.state == 2 && stateKey ==1 && !compare(1)){//from hash to linked
			BasicLinkedList<T> tmp = new BasicLinkedList();
			Object[] items = hash.items();
			for(int i = 0; i < items.length; i++){
				tmp.addInOrder((T)items[i]);
			}
			linked  = tmp;
		}		
		this.state = stateKey;
	}
	// The time constant system is how the data structure adapts to its situations
	// it is computed by multiplying the number of times the strucutre performs an
	// operation by its time complexity with respect to number of elements
	//
	//Honestly idk what I'm going to do with this. I don't think it's the right approach here.
	//I'm also not convinced this is useful functionality
	//I can't think of a situation where I would want the program to randomly switch structures without my command to do so.
	//See last paragraph of progress report
	public void chooseStructure(){
		//best = smallest
		int timeConstantAddArr = addInOrder*(3*numElements); // 3 loops
		int timeConstantAddLink = addInOrder*numElements;
		int timeConstantAddHash = addInOrder; //avg case

		int timeConstantRemoveArr = removeLargest*(2*numElements); //accounts for shift
		int timeConstantRemoveLink = removeLargest*numElements;
		int timeConstantRemoveHash = removeLargest*numElements; //does not require shift

		int timeConstantSearchArr = search*numElements;
		int timeConstantSearchLink = search*numElements;
		int timeConstantSearchHash = search; //avg case

		int sumArr = timeConstantAddArr + timeConstantRemoveArr + timeConstantSearchArr;
		int sumLink = timeConstantAddLink + timeConstantRemoveLink +timeConstantSearchLink;
		int sumHash = timeConstantAddHash + timeConstantRemoveHash + timeConstantSearchHash;

		int smallest = lowest(sumArr,sumLink,sumHash);
		if(numElements>=HASH_THRESHOLD){ //if we've got a lot of data, we're using a hash table. Otherwise, it's crap.
			setState(2);
		}
		else if(smallest == sumArr){
			setState(0);
			}
		else if(smallest == sumLink){
			setState(1);
		}
		else
			setState(0);
		}
		
	public int size(){
		return numElements;
	}
	//finds the smallest of an array of elements
	public int lowest(int... elements){
		int smallest = elements[0]; 
		for(int i: elements){
			if (i < smallest)
				smallest = i;
		}
		return smallest;
	}
	//adds in place
	public void addInPlace(T element, T key){
		if(element == null){
			throw new IllegalArgumentException("Null cannot be added!"); //duh
		}
		if(state == 2 && key != null){
			hash.put(element,key); //add in place meaningless for hashtable.
		}
		else if (state == 0){
			arr.addInOrder(element);
		}
		else if (state == 1){
			linked.addInOrder(element);
		}
		addInOrder++;
		numElements++;
	}
	public void removeLargest(){
		//chooseStructure();
		if (state == 0){
			arr.removeLargest();
		}
		else if(state == 1){
			linked.removeLargest();
		}
		else if(state ==2){
			hash.removeLargest();
		}
		removeLargest++;
		numElements--;
	}
	public Object search(T target){
		//chooseStructure();
		if(state == 0){
			return arr.search(target);
		}
		else if(state == 1){
			return linked.search(linked.getHead(),target);
		}
		else if(state == 2){
			return hash.search(target);
		}
		search++;
		return -1;
	}

	public Object get(T key){
		return hash.get(key);
	}

	public void print(){
		if(state == 0){
			System.out.print("ArrayList: " + arr.toString());
		}
		else if(state ==1){
			System.out.println("LinkedList: "  + linked.toString());
		}
		else if(state == 2){
			System.out.println("HashTable: ");
			hash.print(); //I like this formatting for hashtable better
		}
	}
	//arguement: which comparison: 0= linked-arr, 1= linked-hash, 2=arr-hash
	//not the best system but it works
	public boolean compare(int a){
		
		if(a == 0){
			LinkedNode cursor = linked.getHead();
			if(linked.size() != arr.size())
				return false;
			for(int i = 0; i < arr.size(); i++){
				if(cursor.getData().compareTo(arr.get(i)) != 0)
					return false;
				cursor = cursor.getLink();

			}
			return true;
		}
		else if(a==1){
			LinkedNode cursor = linked.getHead();
			if(linked.size() != hash.size()){
				return false;
			}
			return compareArrays(linked.items(),arr.items());
		}
		else if(a==2){
			return compareArrays(hash.items(),arr.items());
			}	
		
		else
			throw new IllegalArgumentException("Bad arguement");
			
	}
	//compares arrays with hashset
	public static boolean compareArrays(Object[] arr1, Object[] arr2) {
    	HashSet<Object> set1 = new HashSet<Object>(Arrays.asList(arr1));
    	HashSet<Object> set2 = new HashSet<Object>(Arrays.asList(arr2));
    	return set1.equals(set2);
	}

	public void add(T element,T key){
		if(this.state == 0){
			arr.add(element);
		}
		else if(this.state == 1){
			linked.add(element);
		}
		else if(this.state == 2){
			hash.put(element,key);
		}
		else
			throw new IllegalStateException("state/arguement combo not correct");
		numElements++;
	}



}