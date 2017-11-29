package interfaceApplication;

import java.io.FileOutputStream;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import JGrapeSystem.rMsg;
import apps.appsProxy;
import httpServer.grapeHttpUnit;
import interfaceController.interfaceUnit;
import interfaceModel.GrapeDBSpecField;
import interfaceModel.GrapeTreeDBModel;
import offices.excelHelper;
import privacyPolicy.privacyPolicy;
import rpc.execRequest;
import security.codec;
import string.StringHelper;

public class Content {
	private GrapeTreeDBModel group;
	private GrapeDBSpecField gDbSpecField;

	public Content() {
		group = new GrapeTreeDBModel();
		gDbSpecField = new GrapeDBSpecField();
		gDbSpecField.importDescription(appsProxy.tableConfig("ContentLog"));
		group.descriptionModel(gDbSpecField);
		group.bind();
	}
	
	private void filterJSON(JSONObject json){
		String tempCaption;
		for(int i =0; i< 20; i++){
			String title = "";
			String content = "";
			tempCaption = "title_" + String.valueOf(i);
			JSONObject tempJson;
			if( json.containsKey( tempCaption )){
				tempJson = json.getJson(tempCaption);
				title = tempJson.getString("content");
			}
			tempCaption = "content_" + String.valueOf(i);
			if( json.containsKey( tempCaption )){
				tempJson = json.getJson(tempCaption);
				content = tempJson.getString("content");
				String url = tempJson.getString("url");
				privacyPolicy pp = new privacyPolicy();
				System.out.println("正在扫描。。。。");
				pp.scanText(content);
				System.out.println("扫描结束");
				System.out.println(group.formName );
				
				if( pp.hasPrivacyPolicy() ){
					group.data( (new JSONObject("title",title)).puts("url",url).puts("type",0) ).autoComplete().insertOnce();
				}else{
					group.data( (new JSONObject("title",title)).puts("url",url).puts("type",1) ).autoComplete().insertOnce();
					System.out.println("不包含隐私内容");
				}
			}
		}
	}
	
	public String SetInfo(String id) {
		JSONObject object = JSONObject.toJSON(execRequest.getChannelValue(grapeHttpUnit.formdata).toString());
		String info = object.getString("param");
		return SetInfo(id,info);
	}
	
	
	public String SetInfo(String id,String info) {
		String result = rMsg.netMSG(100, "无效参数A:" + info);
		info = codec.DecodeFastJSON(info);
		System.out.println(info);
		JSONObject obj = JSONObject.toJSON(info);
		if (obj != null && obj.size() > 0) {
				filterJSON(obj);
				result = rMsg.netMSG(0, "调用成功");
		}
		return result;
	}
	
	//到处详细信息
	@SuppressWarnings("unchecked")
	public String outExcel() {
		String rString = "生成失败";
		String path ="C://temp.xls";
	    byte[] by;
		try {
			org.json.simple.JSONArray dataArray = group.eq("type", 0).select();
			if( dataArray != null && dataArray.size() > 0 ){
				by = excelHelper.out(dataArray.toJSONString());
				FileOutputStream fos = new FileOutputStream(path);
				fos.write(by);
				fos.close();
				rString = "生成成功";
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return rString;
	}
	
//	导出统计数据
	@SuppressWarnings("unchecked")
	public String outExcelCount() {
		String path ="C://tempCount.xls";
	    byte[] by;
		try {
			long pNo = group.eq("type", 0).count();
			long nNo = group.count();
			JSONObject json = (new JSONObject("包含隐私信息文章",pNo)).puts("文章总数",nNo);
			JSONArray dataArray = new JSONArray();
			dataArray.add(json);
			by = excelHelper.out(dataArray.toJSONString());
			FileOutputStream fos = new FileOutputStream(path);
			fos.write(by);
			fos.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}
	/*
	 * 
	 * */
}
