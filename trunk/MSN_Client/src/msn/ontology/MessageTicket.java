package msn.ontology;

import jade.content.Concept;
import jade.content.frame.FrameException;
import jade.content.frame.OrderedFrame;
import jade.content.frame.SLFrameCodec;
import jade.util.leap.ArrayList;
import jade.util.leap.Map;

import java.io.IOException;

import msn.client.MSNAgent;
import msn.client.StringTokenizer;

import com.sun.midp.io.Base64;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE

public class MessageTicket implements Concept {
	
	private final String SEPARATOR1="|";
	private final String SEPARATOR2="-";

    private String destinationRequest;
    private String type;
    private String content;
    private byte[] data;
    private int order;
    private int total;



	public static final String ADD_FRIEND_MSG="Add me as friend";
    public static final String NULLCONT="*******";

    public MessageTicket (String destinationRequest, String type ,String content, byte [] data){
        this.destinationRequest=destinationRequest;
        this.type=type;
        if (content==null || content.equals(""))
        	this.content=NULLCONT;
        else
        	this.content=content;
        this.data=data;
        this.order=0;
        this.total=1;
    }
    
    public MessageTicket (String destinationRequest, String type ,String content, byte [] data,int order, int total){
        this.destinationRequest=destinationRequest;
        this.type=type;
        if (content==null || content.equals(""))
        	this.content=NULLCONT;
        else
        	this.content=content;
        this.data=data;
        this.order=order;
        this.total=total;
    }
    
    public MessageTicket (){
    }
    
    public MessageTicket (String not){
    	try {
	    	StringTokenizer st=new StringTokenizer(not, SEPARATOR2);
	    	destinationRequest=st.nextToken();
	    	type=st.nextToken();
	    	content=st.nextToken();
	    	String d=st.nextToken();
	    	if(d.equals(NULLCONT))
	    		data=new byte [0];
			else
				data=Base64.decode(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public String getDestinationRequest(){
        return destinationRequest;
    }

    public String getContent(){
        return content;
    }
    
    public String getType(){
        return type;
    }
    
    public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getSEPARATOR1() {
		return SEPARATOR1;
	}

	public String getSEPARATOR2() {
		return SEPARATOR2;
	}

	public void setDestinationRequest(String destinationRequest) {
		this.destinationRequest = destinationRequest;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setContent(String content) {
		this.content = content;
	}
    
    public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int hashCode(){
    	String s= destinationRequest + SEPARATOR1 + type + SEPARATOR1 + content +SEPARATOR1+order+SEPARATOR1+total;
    	return s.hashCode();
    }
    
    public String toString(){
    	String d="";
    	if (data.length==0){
    		d=NULLCONT;
    	}else{
    		d=Base64.encode(data, 0, data.length);
    	}
    	String s=destinationRequest + SEPARATOR2 + type + SEPARATOR2 + content + SEPARATOR2 + d + SEPARATOR2 + order + SEPARATOR2 + total;
    	return s;
    }
    
    public String getOrderKey(){
    	String s=destinationRequest + SEPARATOR2 + type + SEPARATOR2 + content + SEPARATOR2 + order + SEPARATOR2 + total;
    	return s;
    }
    
    public String getNotificationKey(){
    	String s=destinationRequest + SEPARATOR2 + type + SEPARATOR2 + content + SEPARATOR2 + total;
    	return s;
    }
    
    public String encodeFrames() throws FrameException{
    	String d="";
    	if (data.length==0){
    		d=NULLCONT;
    	}else{
    		d=Base64.encode(data, 0, data.length);
    	}
    	String ord="ORDER"+order;
    	String tot="TOTAL"+total;
    	
    	SLFrameCodec slfc=new SLFrameCodec();
    	OrderedFrame notificationFrame = new OrderedFrame (MSNVocabulary.NOTIFICATION);
    	OrderedFrame destinationRequestFrame = new OrderedFrame (MSNVocabulary.DESTINATIONREQUEST);
    	destinationRequestFrame.addElement(destinationRequest);
    	OrderedFrame typeFrame = new OrderedFrame (MSNVocabulary.TYPE);
    	typeFrame.addElement(type);
    	OrderedFrame contentFrame = new OrderedFrame (MSNVocabulary.CONTENT);
    	contentFrame.addElement(content);
    	OrderedFrame orderFrame = new OrderedFrame (MSNVocabulary.ORDER);
    	orderFrame.addElement(ord);
    	OrderedFrame totalFrame = new OrderedFrame (MSNVocabulary.TOTAL);
    	totalFrame.addElement(tot);
    	OrderedFrame dataFrame = new OrderedFrame (MSNVocabulary.DATA);
    	dataFrame.addElement(d);
    	notificationFrame.addElement(destinationRequestFrame);
    	notificationFrame.addElement(typeFrame);
    	notificationFrame.addElement(contentFrame);
    	notificationFrame.addElement(orderFrame);
    	notificationFrame.addElement(totalFrame);
    	notificationFrame.addElement(dataFrame);
    	return slfc.encode(notificationFrame);
    }
    
    public void decodeFrames(String frames) throws FrameException {
    	SLFrameCodec slfc=new SLFrameCodec();
    	OrderedFrame frame= (OrderedFrame)slfc.decode(frames);

    	
    	String e1=(String) ((OrderedFrame)frame.elementAt(0)).elementAt(0);
    	String e2=(String) ((OrderedFrame)frame.elementAt(1)).elementAt(0);
    	String e3=(String) ((OrderedFrame)frame.elementAt(2)).elementAt(0);
    	String e4=(String) ((OrderedFrame)frame.elementAt(3)).elementAt(0);
    	String e5=(String) ((OrderedFrame)frame.elementAt(4)).elementAt(0);
    	String e6=(String) ((OrderedFrame)frame.elementAt(5)).elementAt(0);
    	

    	this.setDestinationRequest(e1);
    	this.setType(e2);
    	this.setContent(e3);
    	this.setOrder(Integer.parseInt(e4.substring(5)));
    	this.setTotal(Integer.parseInt(e5.substring(5)));
    	
    	if (e6.equals(NULLCONT)){
    		this.data=new byte [0];
    	}else{
	    	try {
				this.setData(Base64.decode(e6));
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }

	public String translate() {
		String s="";
		if (type.equals(MSNAgent.FRIEND_REQUEST))
			s=content + " asked you if you want to be his friend";
		else if (type.equals(MSNAgent.PROFILE_RESPONSE))
			s="You have received the profile from "+destinationRequest;
		else if (type.equals(MSNAgent.WALL_RESPONSE))
			s="You have received the wall from "+ destinationRequest;
		else if (type.equals(MSNAgent.WALLFILE_RESPONSE))
			s="You have received the file " +content+ " from " + destinationRequest;
		else if (type.equals(MSNAgent.SEARCH_RESPONSE))
			s="You have received a response for your search: " +content+ ". From " + destinationRequest;
		else if (type.equals(MSNAgent.FRIEND_ACCEPTATION))
			s=content + " is now your friend";
		else if (type.equals(MSNAgent.FRIEND_DELETE))
			s=content + " deleted you from friend his list";
		else if (type.equals(MSNAgent.ERROR_FILE))
			s="The file: "+content+ " does not exists anymore on the friend device";
		return s;
	}
	
	public static ArrayList splitMessage(MessageTicket n, int dim){
		ArrayList al=new ArrayList();
		byte b[]=n.getData();
		
		if (b.length<dim)
			al.add(n);
		else{
			double rem=b.length % dim;
			int elements=b.length / dim;
			int totalDim=elements;
			if (rem!=0){
				totalDim++;
			}
			
			for (int i=0; i<elements; i++){
				byte[] el=new byte[dim];
				for (int k=0; k<dim; k++){
					el[k]=b[k+(dim*i)];
				}
				al.add(new MessageTicket(n.getDestinationRequest(), n.getType(), n.getContent(), el,i,totalDim));
			}
			if (rem!=0){
				int remaining=b.length-(dim*elements);
				byte[] el=new byte[remaining];
				for (int k=0; k<remaining; k++){
					el[k]=b[k+(dim*(elements))];
				}
				al.add(new MessageTicket(n.getDestinationRequest(), n.getType(), n.getContent(), el,elements,totalDim));
			}
		}
		
		
		return al;	
	}
	
	public static MessageTicket mergeMessages(Map map, MessageTicket sample){
		int dimension=0;
		byte[] b=new byte[dimension];
		for (int i=0;i<map.size();i++){
			MessageTicket n=(MessageTicket)map.get(new Integer(i));
			byte[] elemnt=n.getData();
			dimension+=elemnt.length;
		}
		b=new byte[dimension];
		int counter=0;
		for (int i=0;i<map.size();i++){
			MessageTicket n=(MessageTicket)map.get(new Integer(i));
			byte[] elemnt=n.getData();
			for (int k=0;k<elemnt.length;k++){
				b[counter]=elemnt[k];
				counter++;
			}			
		}
		
		return new MessageTicket(sample.getDestinationRequest(),sample.getType(),sample.getContent(),b);
	}

}
