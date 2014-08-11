public class Node
{
    int data;           // ‚±‚Ìß‚Ìƒ‰ƒxƒ‹
    Node    left;           // ¶•”•ª–Ø
    Node    right;          // ‰E•”•ª–Ø

    /**
     * “ñ•ª–Ø‚Ìß‚ğ¶¬‚·‚é
     *
     * @param data     ƒ‰ƒxƒ‹
     */
    Node(Integer data)
    {	

        left       = null;
        right      = null;
    	this.data  = data.intValue();
    }
}