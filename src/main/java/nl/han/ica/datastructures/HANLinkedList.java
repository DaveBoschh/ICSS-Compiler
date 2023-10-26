package nl.han.ica.datastructures;

public class HANLinkedList<T> implements IHANLinkedList<T> {
    Node<T> head;

    @Override
    public void addFirst(T value) {
        Node<T> newNode = new Node<T>(value);
    newNode.setNext(this.head);
    this.head = newNode;
    }


    @Override
    public void clear() {
        this.head.setNext(null);
        this.head.setData(null);

    }

    @Override
    public void insert(int index, T value) {
        if(this.head == null && index == 0){
            addFirst(value);
            return;
        }
        Node<T> currNode = head;
        Node<T> prevNode = null;
        Node<T> newNode = new Node<T>(value);
        int counter = 0;
        if(index == getSize()){
            while (currNode.hasNext()){
                currNode = currNode.getNext();
            }
            currNode.setNext(newNode);
            return;
        }
        while (currNode != null){
            if (counter == index){
                newNode.setNext(currNode);
                prevNode.setNext(newNode);
                newNode.setNext(currNode);
                return;
            }else{
                prevNode = currNode;
                currNode = currNode.getNext();
                counter++;
            }
        }
        return;

    }

    @Override
    public void delete(int pos) { // NIET ZEKER OF DIT WERKT :(
        var currNode = head;
        Node<T> prevNode = null;

        if (pos == 0 && currNode != null) {  //first pos
            removeFirst();
            return;
        }
        int counter = 0;
        while (currNode != null) {
            if (counter == pos) {
                prevNode.setNext(currNode.getNext());
                return;
            } else {
                prevNode = currNode;
                currNode = currNode.getNext();
                counter++;
            }

        }
        return;

    }

    @Override
    public T get(int pos) {
        var currNode = head;
        int counter = 0;
        while(currNode != null) {
            if (counter == pos) {
                return currNode.getData();
            } else {
                currNode = currNode.getNext();
                counter++;
            }
        }
        return null;
    }

    @Override
    public void removeFirst() {
        this.head = this.head.getNext();

    }

    @Override
    public T getFirst() {
        if(this.head == null){
            return null;
        }
        return head.getData();
    }

    @Override
    public int getSize() {
        int counter = 0;
        Node<T> currNode = head;
        if (currNode != null) {
            counter++;
            while (currNode.hasNext()) {
                currNode = currNode.getNext();
                counter++;

            }
        }
        return counter;
    }
}
