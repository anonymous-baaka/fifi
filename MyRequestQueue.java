import java.util.ArrayList;

class Request
{
    String timestamp;
    String clientIp;
    int tokenNumber;
    int windowNumber;
    public enum Status{
        WAITING,PROCESSING,COMPLETED,
    }
    Status status;
    Request(String _timestamp,String _clIp,int _tokenNumber,int _windowNumber)
    {
        clientIp=_clIp;
        timestamp=_timestamp;
        status=Status.WAITING;
        tokenNumber=_tokenNumber;
        windowNumber=_windowNumber;
    }
}

public class MyRequestQueue {
    int len;
    int ptr;
    public ArrayList<Request> arr=new ArrayList<Request>();
    MyRequestQueue()
    {
        len=0;
        ptr=-1;
    }
    public ArrayList<Request>getArray()
    {
        ArrayList<Request>tmp=new ArrayList<Request>();
        for(Request r:arr)
            tmp.add(r);
        return tmp;
    }
    void push(Request request)
    {
        arr.add(request);
        ptr++;
        len++;
    }
    Request pop()
    {
        Request req=null;
        if(ptr<=0)
        {
            req=arr.remove(arr.size()-1);
            ptr--;
            len--;
        }
        return req;
        
    }

    public boolean updateQueueWindowNumber(int token,int window)
    {
        for(int i=0;i<arr.size();i++)
        {
            if(arr.get(i).tokenNumber==token) 
            {
                arr.get(i).windowNumber=window;
                return true;
            }
        }
        return false;
    }
    Boolean pop(Request request)
    {
        for(int i=0;i<arr.size();i++)
        {
            if(arr.get(i).tokenNumber==request.tokenNumber && arr.get(i).timestamp==request.timestamp && arr.get(i).clientIp==request.clientIp)
            {
                arr.get(i).status=Request.Status.COMPLETED;
                return true;
            }
        }
        return false;
    }

    Boolean update(Request request,char ch)
    {

       for(int i=0;i<arr.size();i++)
        {
            if(arr.get(i).tokenNumber==request.tokenNumber && arr.get(i).timestamp==request.timestamp && arr.get(i).clientIp==request.clientIp)
            {
                //arr.remove(i);
                if(ch=='W')
                    arr.get(i).status=Request.Status.WAITING;
                else if(ch=='P')
                    arr.get(i).status=Request.Status.PROCESSING;
                else
                    arr.get(i).status=Request.Status.COMPLETED;

                return true;
            }
        }
        return false;
    }

    void display()
    {
        for(Request req: arr)
        {
            System.out.println(req.tokenNumber+"\t"+req.clientIp+"\t"+req.timestamp);
        }
    }
    int size()
    {
        return arr.size();
    }
}

