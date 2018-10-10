# 生成证书，此处CN为前面设定的host的域名
keytool -genkey -alias cas -keyalg RSA -keysize 2048 -keypass sso123456 -storepass sso123456 -keystore /etc/cas/config/keystore -dname "CN=cas.example.org,OU=sso.com,O=sso,L=Beijing,ST=ShangHai,C=CN"
#导出证书
keytool -exportcert -alias cas -keystore /etc/cas/config/keystore -file /etc/cas/config/cas.cer -storepass sso123456
#导入证书到java cacerts中
keytool -import -alias cas -keystore /etc/cas/config/cacerts -file /etc/cas/config/cas.cer
#删除证书
keytool -delete -alias cas -keystore /etc/cas/config/cacerts

-alias       产生别名
-keystore    指定密钥库的名称(产生的各类信息将不在.keystore文件中)
-keyalg      指定密钥的算法 (如 RSA  DSA（如果不指定默认采用DSA）)
-validity    指定创建的证书有效期多少天
-keysize     指定密钥长度
-storepass   指定密钥库的密码(获取keystore信息所需的密码)
-keypass     指定别名条目的密码(私钥的密码)
-dname       指定证书拥有者信息 例如：  "CN=名字与姓氏,OU=组织单位名称,O=组织名称,L=城市或区域名称,ST=州或省份名称,C=单位的两字母国家代码"
-list        显示密钥库中的证书信息      
-v           显示密钥库中的证书详细信息
-export      将别名指定的证书导出到文件  
-file        参数指定导出到文件的文件名
-delete      删除密钥库中某条目         
-printcert   查看导出的证书信息          
-keypasswd   修改密钥库中指定条目口令   
-storepasswd 修改keystore口令     
-import      将已签名数字证书导入密钥库
