package postfile;


import net.sf.json.JSONObject;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import serverutils.ServerUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.List;

@WebServlet(name = "PostFile")
public class PostFile extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        System.out.println("上传文件");

        String jsonString = ServerUtils.inputStreamToString(request.getInputStream());
        jsonString = ServerUtils.httpBase64Decode(jsonString);
        JSONObject jsonObject = JSONObject.fromObject(jsonString);

        String extension = (String)jsonObject.get("extension");
        String file = (String) jsonObject.get("file");
        //byte[]转String转byte[]不指定编码会导致前后长度不一致
        byte[]content = file.getBytes("ISO-8859-1");

        String fileName = ServerUtils.md5Encode(content) + "." + extension;
        List<String> list = ServerUtils.getDataPath();
        ServerUtils.save(content, ServerUtils.toFilePath(list, ServerUtils.OS), fileName);

        System.out.println("转换资源地址: "+ServerUtils.SERVER_PATH + "/" + ServerUtils.toFilePath(list, "linux") + "/" + fileName);
        String ans = ServerUtils.SERVER_PATH + "/" + ServerUtils.toFilePath(ServerUtils.getRelativeDataPath(), "linux") + "/" + fileName;

        jsonObject = new JSONObject();
        jsonObject.put("address", ans);

        out.write(ServerUtils.httpBase64Encode(jsonObject.toString()));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
