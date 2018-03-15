package interfaceApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import Test.Shuju;
import common.java.JGrapeSystem.rMsg;
import common.java.apps.appsProxy;
import common.java.check.checkHelper;
import common.java.httpClient.request;
import common.java.httpServer.grapeHttpUnit;
import common.java.interfaceModel.GrapeDBSpecField;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.nlogger.nlogger;
import common.java.offices.excelHelper;
import common.java.privacyPolicy.privacyPolicy;
import common.java.rpc.execRequest;
import common.java.security.codec;
import common.java.string.StringHelper;
import common.java.time.TimeHelper;
import unit.Ceshi;
import unit.DocUtils;
import unit.DocxUtils;
import unit.ExcelUtils;
import unit.FileAndByte;
import unit.FileModel;
import unit.FileTypeHelper;
import unit.FileUtils;
import unit.HtmlUtils;
import unit.PdfUtils;
import unit.Print_nlogger;
import unit.TXTUtils;
import unit.ZipUtils;

public class Content {

	private GrapeTreeDBModel group;
	private GrapeDBSpecField gDbSpecField;
	private String filePath;
	private int count = 1;
	private int c = 1;
	private int m = 1;
	private JSONArray jsonarray = new JSONArray();
	private String wbName;
	private String ogName;

	public Content() {

		group = new GrapeTreeDBModel();
		gDbSpecField = new GrapeDBSpecField();
		gDbSpecField.importDescription(appsProxy.tableConfig("ContentLog"));

		group.descriptionModel(gDbSpecField);
		group.bind();
		// init();
		filePath = "C:\\ckd\\CrawlerContent\\Content\\";
	}

	public void init() {
		Properties pro = new Properties();

		FileInputStream in = null;
		try {
			in = new FileInputStream("CrawlerContent.properties");
			pro.load(in);
			filePath = pro.getProperty("filePath");
		} catch (IOException e) {
			nlogger.logout(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				nlogger.logout(e);
			}
		}

	}
	public String syso_test_remote_filterJSON(String a,String b) {
		Print_nlogger.Print_SYSO("陈凯迪2");
		return "陈凯迪2";
	}

	public String syso_test_remote_filterJSON(String a,String b,String c) {
		Print_nlogger.Print_SYSO("陈凯迪3");
		return "陈凯迪3";
	}

	public String test_remote_filterJSON(String wbName, String ogName, String exdata) {
//		byte[] postUriFile = request.postUriFile("http://www.ada.gov.cn/download/59dd34d0e4b036d45039314e", "");
//		String fileTypeByStream1 = FileTypeHelper.getFileTypeByStream(postUriFile);
//	byte[] uriFile = request.getUriFile("http://newfile.ahtlyaq.gov.cn:9000/mserver/download/?_id=5a166979706cbd3686eb101a&SiteId=547828059a05c2b435e2dd17" );
//	String fileTypeByStream = FileTypeHelper.getFileTypeByStream(uriFile);
//		FileModel fileModel = new FileModel().getFileModel("http://www.ada.gov.cn/download/59dd34d0e4b036d45039314e");

		this.wbName = wbName;
		this.ogName = ogName;

		JSONObject object = JSONObject.toJSON(exdata);

		String info = object.getString("param");
		String result = rMsg.netMSG(100, "无效参数A:" + info);

		result = filterJSON(info);

		return result;

	}
	public String remote_filterJSON(String wbName, String ogName) {
		Print_nlogger.Print_SYSO(
				"222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222");
		Ceshi.ceshi_write_N(wbName, 2);
		this.wbName = wbName;
		this.ogName = ogName;

		JSONObject object = JSONObject.toJSON(execRequest.getChannelValue(grapeHttpUnit.formdata).toString());

		String info = object.getString("param");
		String result = rMsg.netMSG(100, "无效参数A:" + info);

		result = filterJSON(info);

		return result;

	}

	public String remote_filterJSON(String wbName, String ogName, String exdata) {
		Print_nlogger.Print_SYSO(
				"33333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333");
		Ceshi.ceshi_write_N(wbName, 2);
		this.wbName = wbName;
		this.ogName = ogName;

		JSONObject object = JSONObject.toJSON(execRequest.getChannelValue(grapeHttpUnit.formdata).toString());

		String info = object.getString("param");
		String result = rMsg.netMSG(100, "无效参数A:" + info);

		result = filterJSON(info);

		return result;

	}

	private String filterJSON(String info) {
		JSONObject obj = getArticle1(info);
		String path = "c:\\ckd_yinsi\\ckd\\" + wbName + "\\" + ogName + "\\";
		count_article(obj, path);

		for (Object key : obj.keySet()) {
			Print_nlogger.Print_SYSO(count++);
			String content = "", url = "", mainName = "";
			JSONObject json = obj.getJson(key);
			if (json.containsKey("content")) {
				content = json.getString("content");
			}
			if (json.containsKey("url")) {
				url = json.getString("url");
			}
			if (json.containsKey("mainName")) {
				mainName = json.getString("mainName");
				mainName = chuliMainName(mainName);
			}
			boolean if_String_hasPrivacy = if_String_hasPrivacy(content);

			if (if_String_hasPrivacy) {
				JSONObject jsonobj = (new JSONObject("mainName", mainName)).puts("url", url);
				// group.data(jsonobj).autoComplete().insertOnce();
				jsonarray.add(jsonobj);

			}

		}
		ExcelUtils.jsonarray_to_Excel(jsonarray, path, UUID.randomUUID() + ".xls");

		return rMsg.netMSG(30, "结束");

		// Print_nlogger.Print_SYSO(group.formName);
	}

	private static  void count_article(JSONObject obj, String path) {
		long count = 0;
		int size = obj.size();
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		File file2 = new File(path + "文章总数.txt");
		if (file2.exists()) {
			String num = TXTUtils.Txt2String(file2.getPath());
			if(StringHelper.InvaildString(num)) {
				num=num.trim();
			count = Long.parseLong(num);
				
			}

		}
		long sum=count+size;
		TXTUtils.string2Txt(path + "文章总数.txt", String.valueOf(sum), "UTF-8");

	}

	private String chuliMainName(String s) {
		char[] charArray = s.toCharArray();

		for (int i = 0; i < charArray.length; i++) {
			if ('・' == charArray[i]) {
				s = s.substring(i + 1);
				break;
			}
		}
		return s;
	}

	/**
	 * 封装文章数据1
	 * 
	 * @param info
	 * @return {"url":{"mainName":"","content":""...}}
	 */
	private JSONObject getArticle1(String info) {
		String decodeFastJSON = codec.DecodeFastJSON(info);
		JSONObject obj = JSONObject.toJSON(codec.DecodeHtmlTag(decodeFastJSON));

		JSONObject rNewArray = new JSONObject();// 新信息数组
		int i = 0;
		for (Object _obj : obj.keySet()) {// _obj = mainName_XXX
			JSONObject json = obj.getJson(_obj);
			if (json != null && json.size() > 0) {
				String[] item = ((String) _obj).split("_");
				String url = json.getString("url");
				String key = (item.length == 2) ? item[1] : "0";
				Object val = json.getString("content");
				String mainName = item[0];
				JSONObject dataJson = (JSONObject) rNewArray.get(key);
				if (dataJson != null && dataJson.size() > 1) {
					i = 100;
				}
				dataJson = appendInfo(dataJson, item[0], val);
				if (!dataJson.containsKey("url")) {
					if ("mainName".equals(mainName)) {
						dataJson.puts("url", url);
					}

				}
				rNewArray.put(key, dataJson);
			}
		}

		// 构造有序结构组
		return rNewArray;
	}

	private JSONObject appendInfo(JSONObject data, String key, Object val) {
		if (data == null) {
			data = new JSONObject();
		} else {
			String aa = "asd";
		}
		if (!data.containsKey(key)) {
			data.put(key, val);
		}
		return data;
	}

	public boolean if_String_hasPrivacy(String content) {

		boolean if_File_hasPrivacy = false;
		privacyPolicy pp = new privacyPolicy();
		String content1 = HtmlUtils.getHtml_Exclude_href_src(content);

		pp.scanText(content1);
		if (pp.hasPrivacyPolicy()) {
			// 如果文章内容本身就有隐私内容就不用管href了
			return true;
		}
		List<String> hrefList = HtmlUtils.getLink_then_getHref(content);
		if (hrefList != null && hrefList.size() > 0) {
			// for (String url : hrefList) {
			// if (url.endsWith(".doc") || url.endsWith(".docx") || url.endsWith(".xls") ||
			// url.endsWith(".xlsx")
			// || url.endsWith(".html") || url.endsWith(".pdf") || url.endsWith(".txt")
			// || url.endsWith(".zip")) {
			// if_File_hasPrivacy = if_File_hasPrivacy(url);
			// // 只要有一个url下载的是隐私文件就停止扫描后面的url
			// if (if_File_hasPrivacy) {
			// return if_File_hasPrivacy;
			// }
			// }
			//
			// }
			for (String url : hrefList) {
				if_File_hasPrivacy = if_File_hasPrivacy(url);

			}
		}
		
		return if_File_hasPrivacy;

	}
	public Object if_File_hasPrivacy_rm(String url) {
		boolean if_File_hasPrivacy = if_File_hasPrivacy(url);
		
		return  rMsg.netMSG(if_File_hasPrivacy?1:0, if_File_hasPrivacy?"是隐私链接":"不是隐私链接");
	}
	public Object if_String_hasPrivacy_rm(String content) {
		boolean if_String_hasPrivacy = if_String_hasPrivacy(content);
		
		return  rMsg.netMSG(if_String_hasPrivacy?1:0, if_String_hasPrivacy?"有隐私链接":"没有隐私链接");
	}

	public boolean if_File_hasPrivacy(String url) {
		// url = codec.DecodeFastJSON(url);
		// url =
		// "https://mmbiz.qpic.cn/mmbiz_jpg/Bic8edlgthJHtVlGCJoa9ds5E36ibAq1OAgflRhglUiagX4V20BlcG9ib6mYOcZuFeQmIZtGtIN3KxMEQlJo2VBGWA/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1";
	
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		FileModel fileModel = new FileModel().getFileModel(url);
		
		byte[] uriFile = fileModel.getUrlFile();
		if (uriFile == null || uriFile.length == 0) {// 跳转别的网页的超链接,不是下载文件的超链接,扫描html网页(或者可能是文件链接失效)
			// String html = request.page(url);
			// if(!StringHelper.InvaildString(html)) {//html没响应
			// return false;
			// }
			// boolean if_String_hasPrivacy = if_String_hasPrivacy(html);
			// return if_String_hasPrivacy;
			return false;// 文件链接失效
		}
		String fileType = getSuffix(url);
		if (fileType == null) {
			fileType = fileModel.getFileType();


		}
		Print_nlogger.Print_SYSO("文件类型:" + fileType);
		if (!"txt".equals(fileType) && !"doc".equals(fileType) && !"docx".equals(fileType) && !"xls".equals(fileType)
				&& !"xlsx".equals(fileType) && !"pdf".equals(fileType) && !"html".equals(fileType)
				&& !"zip".equals(fileType)) {
			return false;
		}

		String fileName = new StringBuffer().append(UUID.randomUUID().toString().replaceAll("-", ""))
				.append(TimeHelper.nowMillis()).append('.').append(fileType).toString();
		FileAndByte.getFile(uriFile, filePath, fileName);
		String whole_filePath = filePath + fileName;
		boolean fileType_select = false;
		
		try {

			fileType_select = fileType_select(fileType, whole_filePath,fileModel);
		} catch (Exception e) {
			nlogger.logout(e);
		} finally {
			File file_x = new File(whole_filePath);
			FileUtils.deleteAllFilesOfDir(file_x);

		}
      
		return fileType_select;
	}

	private String getSuffix(String url) {
		if (url.endsWith(".txt")) {
			return "txt";
		}
		if (url.endsWith(".doc")) {
			return "doc";
		}
		if (url.endsWith(".docx")) {
			return "docx";
		}
		if (url.endsWith(".xls")) {
			return "xls";
		}
		if (url.endsWith(".xlsx")) {
			return "xlsx";
		}
		if (url.endsWith(".pdf")) {
			return "pdf";
		}
		if (url.endsWith(".html")) {
			return "html";
		}
		if (url.endsWith(".zip")) {
			return "zip";
		}
		return null;

	}

	public boolean fileType_select(String fileType, String whole_filePath,FileModel fileModel) {
		switch (fileType) {
		case "txt":
			boolean chuli_txt = chuli_txt(whole_filePath,fileModel);
			return chuli_txt;
		case "doc":
			boolean chuli_doc = chuli_doc(whole_filePath,fileModel);
			return chuli_doc;
		case "docx":
			boolean chuli_docx = chuli_docx(whole_filePath,fileModel);
			return chuli_docx;
		case "xls":
			boolean chuli_xls = chuli_xls(whole_filePath,fileModel);
			return chuli_xls;
		case "xlsx":
			boolean chuli_xlsx = chuli_xlsx(whole_filePath,fileModel);
			return chuli_xlsx;
		case "pdf":
			boolean chuli_pdf = chuli_pdf(whole_filePath,fileModel);
			return chuli_pdf;
		case "html":
			boolean chuli_html = chuli_html(whole_filePath,fileModel);
			return chuli_html;
		case "zip":
			boolean chuli_zip = chuli_zip(whole_filePath,fileModel);
			return chuli_zip;
		default:
			return false;

		}

	}

	private boolean chuli_zip(String whole_filePath,FileModel fileModel) {
		int lastIndexOf = whole_filePath.lastIndexOf("\\");
		String substring = whole_filePath.substring(0, lastIndexOf);
		ZipUtils.decompression_and_foreach(whole_filePath, substring);
		boolean foreach_zip_Directory = false;
		try {
			foreach_zip_Directory = foreach_zip_Directory(substring,fileModel);
		} catch (Exception e) {
			if (e.getMessage() == "此链接有隐私") {
				foreach_zip_Directory = true;
			}else if(e.getMessage() == "此zip其实是xlsx"){
				String fileName = new StringBuffer().append(UUID.randomUUID().toString().replaceAll("-", ""))
						.append(TimeHelper.nowMillis()).append('.').append("xlsx").toString();
				FileAndByte.getFile(fileModel.getUrlFile(), filePath, fileName);
				String whole_filePath1 = filePath + fileName;
				chuli_xlsx(whole_filePath1,fileModel);
			}else if(e.getMessage()=="此zip其实是docx") {
				String fileName = new StringBuffer().append(UUID.randomUUID().toString().replaceAll("-", ""))
						.append(TimeHelper.nowMillis()).append('.').append("docx").toString();
				FileAndByte.getFile(fileModel.getUrlFile(), filePath, fileName);
				String whole_filePath1 = filePath + fileName;
				chuli_docx(whole_filePath1,fileModel);
			}
			else {
				nlogger.logout(e);
			}

		} finally {
			File file_x = new File(substring);
			FileUtils.deleteAllFilesOfDir(file_x);
		}

		return foreach_zip_Directory;

	}

	private boolean chuli_txt(String whole_filePath,FileModel fileModel) {
		String txt2String = TXTUtils.Txt2String(whole_filePath);
		boolean if_String_hasPrivacy = if_String_hasPrivacy(txt2String);
		return if_String_hasPrivacy;
	}

	private boolean chuli_html(String whole_filePath,FileModel fileModel) {
		String html2Sting = HtmlUtils.html2Sting(whole_filePath);
		boolean if_String_hasPrivacy = if_String_hasPrivacy(html2Sting);
		return if_String_hasPrivacy;

	}

	private boolean chuli_pdf(String whole_filePath,FileModel fileModel) {
		String pdf2String = PdfUtils.pdf2String(whole_filePath);
		boolean if_String_hasPrivacy = if_String_hasPrivacy(pdf2String);
		return if_String_hasPrivacy;

	}

//	private boolean chuli_excel(String whole_filePath,FileModel fileModel) {
//		String excel2String = ExcelUtils.excel2String(whole_filePath);
//		boolean if_String_hasPrivacy = if_String_hasPrivacy(excel2String);
//		return if_String_hasPrivacy;
//
//	}
	private boolean chuli_xlsx(String whole_filePath,FileModel fileModel) {
		String excel2String = ExcelUtils.xlsx_2_String(whole_filePath);
		boolean if_String_hasPrivacy = if_String_hasPrivacy(excel2String);
		return if_String_hasPrivacy;

	}
	private boolean chuli_xls(String whole_filePath,FileModel fileModel) {
		String excel2String = ExcelUtils.xls_2_String(whole_filePath);
		boolean if_String_hasPrivacy = if_String_hasPrivacy(excel2String);
		return if_String_hasPrivacy;

	}

	private boolean chuli_docx(String whole_filePath,FileModel fileModel) {
		String docx2String = DocxUtils.docx2String(whole_filePath);
		boolean if_String_hasPrivacy = if_String_hasPrivacy(docx2String);
		return if_String_hasPrivacy;

	}

	private boolean chuli_doc(String whole_filePath,FileModel fileModel) {
		String doc2String = DocUtils.doc2String(whole_filePath);
		boolean if_String_hasPrivacy = if_String_hasPrivacy(doc2String);
		return if_String_hasPrivacy;

	}

	public boolean foreach_zip_Directory(String strPath,FileModel fileModel) {
		boolean fileType_select = false;
		File dir = new File(strPath);
		File[] files = dir.listFiles();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();
				if (files[i].isDirectory()) {
					boolean foreach_zip_Directory = foreach_zip_Directory(files[i].getAbsolutePath(),fileModel);
					if (foreach_zip_Directory) {
						throw new RuntimeException("此链接有隐私");
					}
				} else {
					if(fileName.contains("workbook.xml")) {
						throw new RuntimeException("此zip其实是xlsx");
					}
					if(fileName.contains("document.xml")) {
						throw new RuntimeException("此zip其实是docx");
					}
					int lastIndexOf = fileName.lastIndexOf(".");
					String fileType = fileName.substring(lastIndexOf + 1);
					String strFileName = files[i].getAbsolutePath();
					fileType_select = fileType_select(fileType, strFileName,fileModel);
					// 只要有一个隐私文件就停止扫描后面的文件
					if (fileType_select) {
						throw new RuntimeException("此链接有隐私");
					}

				}
			}

		}
		return fileType_select;
	}

	public int getexcel_from_mongodb(int i) {
		JSONArray jsonarray_will_to_excel = new JSONArray();
		// jsonarray_will_to_excel.adds(jsonobj);
		String[] s = new String[] { "mainName", "content", "url" };
		JSONArray jsonArray = group.field(s).page(i, 50);
		for (Object object : jsonArray) {
			Print_nlogger.Print_SYSO("第:" + count++);
			JSONObject obj = (JSONObject) object;
			String content = obj.getString("content");
			Print_nlogger.Print_SYSO("当前url:" + obj.getString("url"));
			Print_nlogger.Print_SYSO("当前mainName:" + obj.getString("mainName"));
			boolean if_String_hasPrivacy = if_String_hasPrivacy(content);
			if (if_String_hasPrivacy) {
				JSONObject jsonobj = new JSONObject();
				jsonobj.puts("mainName", obj.getString("mainName"));
				jsonobj.puts("url", obj.getString("url"));
				jsonarray_will_to_excel.adds(jsonobj);

			}

		}

		Print_nlogger.Print_SYSO(jsonarray_will_to_excel);
		ExcelUtils.jsonarray_to_Excel(jsonarray_will_to_excel, "c:\\ckd123\\ckd\\", UUID.randomUUID() + ".xls");
		return jsonArray.size();
	}

	public void getexcel_from_mongodb_loop() {

		int size = 0;
		int i = 11;
		do {
			System.out.println(i);
			size = getexcel_from_mongodb(i);
			i = i + 1;
			if (i > 10) {
				break;
			}
		} while (size > 0);

	}
	public void ExportData11() {
		group.scan(new Function<JSONArray, JSONArray>() {
			
			@Override
			public JSONArray apply(JSONArray t) {
				// TODO Auto-generated method stub
				return null;
			}
		}, 50);
	}
	public Object ExportData() {
		JSONArray rArray = group.scan((array) -> {
			JSONObject json, rJson;
			String content = "";
			JSONArray _rArray = new JSONArray();
			if (array != null && array.size() > 0) {
				for (Object object : array) {
					rJson = new JSONObject();
					json = (JSONObject) object;
					System.out.println(m++);
					if (m > 3000) {
						break;
					}
					content = json.getString("content");
					if (if_String_hasPrivacy(content)) { // 扫描内容，含有隐私数据
						rJson.put("mainName", json.getString("mainName"));
						rJson.put("url", json.getString("url"));
						_rArray.add(rJson);
					}
				}
			}
			return _rArray;
		}, 50);

		// 导出数据
		try {
			if (rArray != null && rArray.size() > 0) {
				ExcelUtils.jsonarray_to_Excel(rArray, "c:\\ckd123\\ckd\\", UUID.randomUUID() + ".xls");
			}
		} catch (Exception e) {
			nlogger.login(e, "导出异常");
		}
		return rMsg.netMSG(false, "");
	}

	public void ExportData1() {
		group.scan(new Function<JSONArray, JSONArray>() {

			@Override
			public JSONArray apply(JSONArray t) {
				// TODO Auto-generated method stub
				return null;
			}

		}, 50);

	}
}
