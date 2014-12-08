/**
 * 
 */
package com.bbkmobile.iqoo.service.baidu.api;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.bbkmobile.iqoo.service.baidu.SearchConstants;
import com.bbkmobile.iqoo.service.baidu.xml.vo.AppVO;
import com.bbkmobile.iqoo.service.baidu.xml.vo.CateVO;
import com.bbkmobile.iqoo.service.baidu.xml.vo.ResultVO;
import com.bbkmobile.iqoo.service.baidu.xml.vo.SearchResultVO;
import com.bbkmobile.iqoo.service.baidu.xml.vo.UpdateResultVO;


/**
 * @author wangbo
 *
 */

@Service("searchXmlProcessor")
@Scope("prototype")
public class SearchXmlProcessor {
    private Log log = LogFactory.getLog(SearchXmlProcessor.class);

    //测试xml中是否包含无效字符
    public boolean CheckUnicodeString(String value) 
    {
        for (int i=0; i < value.length(); ++i) {  
            if (value.charAt(i) > 0xFFFD){  
                System.out.println("Invalid Unicode:" + value.charAt(i));  
                return false;  
            }  
            else if (value.charAt(i) < 0x20 && value.charAt(i) != '\t' && value.charAt(i) != '\n' && value.charAt(i) != '\r'){  
                System.out.println("Invalid Xml Characters:" + value.charAt(i));  
                return false;  
            }  
        }  
        return true;
    }

	public SearchResultVO processSearchXml(String xmlFromBaidu){
		SearchResultVO searchResult = null;
		StringReader sr = null;
		try{
			SAXReader reader = new SAXReader();
			//xmlFromBaidu = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";
			//System.out.println("xmlFromBaidu="+xmlFromBaidu);
			if(null==xmlFromBaidu || xmlFromBaidu.equals("")){
			    return null;
			}
			sr = new StringReader(xmlFromBaidu);
			Document doc = reader.read(sr);
			Element root = doc.getRootElement();
			
			searchResult = parseSearchResult(root);
			
		}catch(Exception e){
			e.printStackTrace();
		    //Lg.error(LgType.STDOUT, "------process baidu search xml error");
		}finally{
			if(null != sr){
				sr.close();
			}
		}
		
		return searchResult;
	}

	

	
	public void AppendAppInfoContentComplete(AppVO app,Element packageEle){
	    packageEle.addElement("package_name").setText(app.getPackagename());
	    packageEle.addElement("parent_id").setText("null");
	    packageEle.addElement("title_zh").setText(app.getSname());
	    packageEle.addElement("title_en").setText(app.getSname());
	    packageEle.addElement("icon_url").setText(app.getIconhdpi());
	    packageEle.addElement("developer").setText(SearchConstants.DEVELOPER_FROM_NETWORK);
	    packageEle.addElement("raters_count").setText(app.getScore_count() + "");
        packageEle.addElement("version_name").setText(app.getVersionname());
        packageEle.addElement("version_code").setText(app.getVersioncode() + "");
        packageEle.addElement("download_count").setText(app.getDownload_count());
	}
	
    
	
	public SearchResultVO parseSearchResult(Element root){
		
		SearchResultVO searchResult = new SearchResultVO();
		List<Element> ls = root.elements();
		
		for(Element ele:ls){
			if(SearchConstants.TAGS_RESPONSE_STATUSCODE.equals(ele.getName())){
				searchResult.setStatuscode(ele.getStringValue());
			}else if(SearchConstants.RESPONSE_STATUSMESSAGE.equals(ele.getName())){
				searchResult.setStatusmessage(ele.getStringValue());
			}else if(SearchConstants.RESPONSE_RESULT.equals(ele.getName())){
				searchResult.setResult(parseResult(ele));
			}else{
				log.warn("未知标签: " + ele.getName());
			}
		}
		
		return searchResult;
	}
	
	private ResultVO parseResult(Element ele){
		ResultVO result = new ResultVO();
		List<Element> ls = ele.elements();
		
		for(Element el:ls){
			if(SearchConstants.RESULT_RN.equals(el.getName())){
				result.setRn(Integer.parseInt(el.getStringValue()));
			}else if(SearchConstants.RESULT_PN.equals(el.getName())){
				result.setPn(Integer.parseInt(el.getStringValue()));
			}else if(SearchConstants.RESULT_DISPNUM.equals(el.getName())){
				result.setDisp_num(Integer.parseInt(el.getStringValue()));   //这个值有时候为null，需要处理
			}else if(SearchConstants.RESULT_RETNUM.equals(el.getName())){
				result.setRet_num(Integer.parseInt(el.getStringValue()));
			}else if(SearchConstants.RESULT_APPS.equals(el.getName())){
				result.setApps(parseApps(el));
			}else if(SearchConstants.RESULT_TITLE.equals(el.getName())){
                result.setTitle(el.getStringValue());
            }else{                                                                    
				log.warn("未知标签: " + el.getName());
			}
		}
		
		return result;
	}
	
	private List<AppVO> parseApps(Element ele){
		List<AppVO> voLs = new ArrayList<AppVO>();
		List<Element> ls = ele.elements();
		
		for(Element el:ls){
			if(SearchConstants.APPS_APP.equals(el.getName())){
				voLs.add(parseApp(el));
			}else{
				log.warn("未知标签: " + el.getName());
			}
		}
		
		
		return voLs;
	}
	
	public AppVO parseApp(Element ele){
		AppVO vo = new AppVO();
		List<Element> ls = ele.elements();
		
		for(Element el:ls){
			if(SearchConstants.APP_SNAME.equals(el.getName())){
				vo.setSname(el.getStringValue());
			}else if(SearchConstants.APP_BIGMAPLINK1.equals(el.getName())){
				vo.setBigmaplink1(el.getStringValue());
			}else if(SearchConstants.APP_BIGMAPLINK2.equals(el.getName())){
				vo.setBigmaplink2(el.getStringValue());
			}else if(SearchConstants.APP_CATEID.equals(el.getName())){
				vo.setCateid(el.getStringValue());
			}else if(SearchConstants.APP_CATENAME.equals(el.getName())){
				vo.setCatename(el.getStringValue());
			}else if(SearchConstants.APP_DESCRIPTION.equals(el.getName())){
				vo.setDescription(el.getStringValue());
			}else if(SearchConstants.APP_DOCID.equals(el.getName())){
				vo.setDocid(el.getStringValue());
			}else if(SearchConstants.APP_FEE.equals(el.getName())){
				vo.setFee(el.getStringValue());
			}else if(SearchConstants.APP_ICON.equals(el.getName())){
				vo.setIcon(el.getStringValue());
			}else if(SearchConstants.APP_DEVELOPERNAME.equals(el.getName())){
                vo.setDevelopername(el.getStringValue());
            }else if(SearchConstants.APP_AUTHENTICATION.equals(el.getName())){
                vo.setAuthentication(el.getStringValue());
            }else if(SearchConstants.APP_LANGUAGE.equals(el.getName())){
				vo.setLanguage(el.getStringValue());
			}else if(SearchConstants.APP_PACKAGE.equals(el.getName())){
				vo.setPackagename(el.getStringValue());
			}else if(SearchConstants.APP_PACKAGEFORMAT.equals(el.getName())){
				vo.setPackageformat(el.getStringValue());
			}else if(SearchConstants.APP_PACKAGESIZE.equals(el.getName())){
				if(null != el.getStringValue() && !el.getStringValue().equals("")){
					vo.setPackagesize(Long.parseLong(el.getStringValue()));
				}else{
					log.warn(el.getName() + "= 空");
				}
			}else if(SearchConstants.APP_PERMISSIONCN.equals(el.getName())){
				vo.setPermission_cn(el.getStringValue());
			}else if(SearchConstants.APP_PLATFORM.equals(el.getName())){
				vo.setPlatform(el.getStringValue());
			}else if(SearchConstants.APP_RELEASEDATE.equals(el.getName())){
				vo.setReleasedate(el.getStringValue());
			}else if(SearchConstants.APP_SCORE.equals(el.getName())){
				vo.setScore(el.getStringValue());
			}else if(SearchConstants.APP_SCORECOUNT.equals(el.getName())){
				vo.setScore_count(el.getStringValue());
			}else if(SearchConstants.APP_SITE.equals(el.getName())){
				vo.setSite(el.getStringValue());
			}else if(SearchConstants.APP_SMALLMAPLINK1.equals(el.getName())){
				vo.setSmallmaplink1(el.getStringValue());
			}else if(SearchConstants.APP_SMALLMAPLINK2.equals(el.getName())){
				vo.setSmallmaplink2(el.getStringValue());
			}else if(SearchConstants.APP_TYPE.equals(el.getName())){
				vo.setType(el.getStringValue());
			}else if(SearchConstants.APP_URL.equals(el.getName())){
				//vo.setUrl(el.getStringValue().replace(" ", "%20"));
			    vo.setUrl(el.getStringValue());
			}else if(SearchConstants.APP_VERSIONCODE.equals(el.getName())){
				vo.setVersioncode(Integer.parseInt(el.getStringValue()));
			}else if(SearchConstants.APP_VERSIONNAME.equals(el.getName())){
				vo.setVersionname(el.getStringValue());
			}else if(SearchConstants.APP_DOWNLAD_COUNT.equals(el.getName())){
                vo.setDownload_count(el.getStringValue());
            }else if(SearchConstants.APP_ICON_LOW.equals(el.getName())){
                vo.setIconlow(el.getStringValue());
            } else if (SearchConstants.APP_ICON_HIGH.equals(el.getName())) {
                vo.setIconhigh(el.getStringValue());
            } else if (SearchConstants.APP_ICON_hdpi.equals(el.getName())) {
                vo.setIconhdpi(el.getStringValue());
            } else if (SearchConstants.APP_ICON_ALADING.equals(el.getName())) {
                vo.setIconalading(el.getStringValue());
            } else if (SearchConstants.APP_ADAPI.equals(el.getName())) {
                vo.setAdapi(el.getStringValue());
            } else if (SearchConstants.ICON_SOURCE.equals(el.getName())) {
                vo.setIcon_source(el.getStringValue());
            } else if (SearchConstants.APP_SIZE.equals(el.getName())) {
                vo.setSize(el.getStringValue());
            } else if (SearchConstants.APP_UPDATETIME.equals(el.getName())) {
                vo.setUpdatetime(el.getStringValue());
            } else if (SearchConstants.APP_SIGNMD5.equals(el.getName())) {
                vo.setSignmd5(el.getStringValue());
            } else {
                log.warn("未知标签: " + el.getName());
            }
		}
		
		return vo;
	}
	
	

    
	
    
   //related recommend
    public UpdateResultVO processRelatedRecXml(String relateRecXml) throws Exception{
        UpdateResultVO relateRecVO = null;
        StringReader sr = null;
        try{
            if(null==relateRecXml || relateRecXml.equals("")){
                return null;
            }
            SAXReader reader = new SAXReader();
            sr = new StringReader(relateRecXml);
            Document doc = reader.read(sr);
            Element root = doc.getRootElement();
            relateRecVO = parseRelateRecResult(root);
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            if(null != sr){
                sr.close();
            }
        }
        return relateRecVO;
    }

    public UpdateResultVO parseRelateRecResult(Element root) {
        UpdateResultVO relateRecVO = new UpdateResultVO();
        List<Element> ls = root.elements();
        for (Element ele : ls) {
            if (SearchConstants.TAGS_RESPONSE_STATUSCODE.equals(ele.getName())) {
                relateRecVO.setStatuscode(ele.getStringValue());
            } else if (SearchConstants.RESPONSE_STATUSMESSAGE.equals(ele.getName())) {
                relateRecVO.setStatusmessage(ele.getStringValue());
            } else if (SearchConstants.RESPONSE_RESULT.equals(ele.getName())) {
                //searchResult.setResult(parseResult(ele));
                List<Element> elements = ele.elements();
                for(Element el:elements){
                   if(SearchConstants.RESULT_APPS.equals(el.getName())){
                       relateRecVO.setApps(parseApps(el));
                    }else{                                                                    
                        log.warn("未知标签: " + el.getName());
                    }
                }
            } else {
                log.warn("未知标签: " + ele.getName());
            }
        }
        return relateRecVO;
    }
    
    
    //category
    public UpdateResultVO processCategoryXml(String categoryXml) throws Exception{
        UpdateResultVO categoryVO = null;
        StringReader sr = null;
        try{
            SAXReader reader = new SAXReader();
            sr = new StringReader(categoryXml);
            Document doc = reader.read(sr);
            Element root = doc.getRootElement();
            categoryVO = parseCategoryResult(root);
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            if(null != sr){
                sr.close();
            }
        }
        return categoryVO;
    }

    public UpdateResultVO parseCategoryResult(Element root) {
        UpdateResultVO categoryVO = new UpdateResultVO();
        List<Element> ls = root.elements();
        for (Element ele : ls) {
            if (SearchConstants.TAGS_RESPONSE_STATUSCODE.equals(ele.getName())) {
                categoryVO.setStatuscode(ele.getStringValue());
            } else if (SearchConstants.RESPONSE_STATUSMESSAGE.equals(ele.getName())) {
                categoryVO.setStatusmessage(ele.getStringValue());
            } else if (SearchConstants.RESULT_CATEGORIES.equals(ele.getName())) {
                categoryVO.setCates(parseCategories(ele));
            } else {
                log.warn("未知标签: " + ele.getName());
            }
        }
        return categoryVO;
    }
    
    private List<CateVO> parseCategories(Element ele){
        List<Element> elements = ele.elements();
        List<CateVO> cates = new ArrayList<CateVO>();
        for(Element el:elements){
            if(SearchConstants.CATEGORY.equals(el.getName())){
                cates.add(parseCategory(el));
            }else{                                                                    
                log.warn("Categories未知标签: " + el.getName());
            }
        }
        return cates;
    }

    private CateVO parseCategory(Element ele){
        CateVO cateVO = new CateVO();
        List<Element> ls = ele.elements();
        
        for(Element el:ls){
            if(SearchConstants.CATE_ID.equals(el.getName())){
                cateVO.setId(el.getStringValue());
            }else if(SearchConstants.CATE_TYPE.equals(el.getName())){
                cateVO.setType(el.getStringValue());
            }else if(SearchConstants.CATE_NAME.equals(el.getName())){
                cateVO.setName(el.getStringValue());
            }else{
                log.warn("category未知标签: " + el.getName());
            }  
        }
        return cateVO;
    }
   
	
}
