#!/usr/bin/env bash
#自动生成资源文件dimens脚本

resoucePath=""
targetPath=""
index='0';

configCount=""  #转换规则条数
origin=""       #原来的 dp sp px
dest=""         #转换后 dp sp px
radio=""        #比例

writeToFile(){
	if [ ! -f $targetPath ]
		then
		touch $targetPath
	fi

    #输出开始标记 开始
	echo '<?xml version="1.0" encoding="utf-8"?>' > $targetPath
	echo '<resources>' >> $targetPath;

	if [[ "${#configCount}" -gt '0' && ${configCount} != '0' ]]
	     then
	         echo "    <!-- 此配置为脚本自动生成 切勿修改 配置参数如下: -->" >> $targetPath
	     else
	         echo "    <!-- 此配置为脚本自动生成 切勿修改 无配置参数 数据copy自 ${resoucePath}   -->" >> $targetPath
	fi

	for i in 0 1 2 3 4 5 6 7 8 9 
	do
		if [ ${#origin[$i]} != 0 ]
			then
				echo "    <!-- ${origin[$[i]]} 转换到 ${dest[$[i]]}  相乘系数: ${radio[$[i]]}  --> "  >> $targetPath
		fi
	done
	#输出开始标记 结束

    cat $resoucePath | while read line
    do
    	# trim 去除头尾空格
        line=`echo ${line} | sed "s/^[ \s]\{1,\}//g;s/[ \s]\{1,\}$//g"`  
        if [[ "${line:0:6}" = '<dimen' ]]
        	then
        	value=${line#*'>'}
        	value=${value%'<'*}
        	value=${value%dp}
        	value=${value%sp}
        	value=${value%px}
        	
        	pre=${line%%'>'*}'>'
        	end='<'${line##*'<'}
        	
            if [[ $(echo $pre | grep "dp") != '' ]] 
            	then
            	trans=''
            	rd=''
            	for i in 0 1 2 3 4 5 6 7 8 9 
            	do
            		if [[ ${origin[$i]} = 'dp' ]]; then
            			trans=${dest[$i]}
            			rd=${radio[$i]}
            			break
            		fi
            	done
            	if [[ ${trans} = '' ]]; then
            		trans='dp'
            	fi
            	if [[ ${rd} = '' ]]; then
            		rd=1
            	fi

                #乘法 保留3位小数
                value=$(echo "scale=3; ${value}*(${rd})"  | bc)

                #补全0
                if [[ ${value:0:1} = '.' ]]
                	then
                	value='0'$value
                fi
            	echo '    '${pre}${value}${trans}${end} >> $targetPath
            elif [[ $(echo $pre | grep "sp") != '' ]] 
                then
                trans=''
            	rd=''
            	for i in 0 1 2 3 4 5 6 7 8 9 
            	do
            		if [[ ${origin[$i]} = 'sp' ]]; then
            			trans=${dest[$i]}
            			rd=${radio[$i]}
            			break
            		fi
            	done
            	if [[ ${trans} = '' ]]; then
            		trans='sp'
            	fi
            	if [[ ${rd} = '' ]]; then
            		rd=1
            	fi

                #乘法 保留3位小数
                value=$(echo "scale=3; ${value}*(${rd})"  | bc)

                #补全0
                if [[ ${value:0:1} = '.' ]]
                	then
                	value='0'$value
                fi
            	echo '    '${pre}${value}${trans}${end} >> $targetPath
            elif [[ $(echo $pre | grep "px") != '' ]] 
            	then
           		trans=''
            	rd=''
            	for i in 0 1 2 3 4 5 6 7 8 9 
            	do
            		if [[ ${origin[$i]} = 'px' ]]; then
            			trans=${dest[$i]}
            			rd=${radio[$i]}
            			break
            		fi
            	done
            	if [[ ${trans} = '' ]]; then
            		trans='px'
            	fi
            	if [[ ${rd} = '' ]]; then
            		rd=1
            	fi

                #乘法 保留3位小数
                value=$(echo "scale=3; ${value}*(${rd})"  | bc)

                #补全0
                if [[ ${value:0:1} = '.' ]]
                	then
                	value='0'$value
                fi
            	echo '    '${pre}${value}${trans}${end} >> $targetPath
            else
            	# 没有配置 转化条件 原样输出
            	echo ${line} >> $targetPath
            fi
        	
        else
            if [[ ${#line} -gt 0 ]]
            then
            	echo "skip line:  $line"
            fi
        fi
    done
    # 输出结束标记
	echo '</resources>' >> $targetPath
}

cat "autoResource.cif" | while read line
do
    echo $line;


	if [[ ${#line} -ge 9 && ${line:0:9} = "-resource" ]]
		then
		resoucePath=${line##*' '}          #获取完整路径 删除左边空格
	    resoucePath=${resoucePath%%' '*}   #获取完整路径 删除右边空格
	    echo "获取原始resouce 路径: $resoucePath"
	    echo " "
	fi

	if [[ ${#line} -ge 9 && ${line:0:9} = "-destPath" ]]
		then

		line=${line#*' '}
		targetPath=${line%%' '*}
		line=${line#*'['}
		line=${line%']'*}

        # 清除原数据
        configCount=0
		for i in 0 1 2 3 4 5 6 7 8 9
		do
			origin[$i]=""
			dest[$i]=""
			radio[$i]=""
		done
        
        # 刷入数据
		arr=($line)
		for str in ${arr[*]}
		do
	     	# 如果包含";"
		    len=${#str}
		    if [ ${str:$[$len-1]:len} = ';' ]
		    	then
		    	str=${str:0:$[len-1]}
		    fi	

		    # 提取参数到数组中
            origin[$configCount]=${str%%'->'*}
            arg=${str#*'->'}
            dest[$configCount]=${arg%%'->'*}
            radio[$configCount]=${str##*'->'}
   
            echo "config index $index : item $configCount origin=${origin[$configCount]}  dest=${dest[$configCount]}  radio=${radio[$configCount]}"
            configCount=$[$configCount+1]
		done

        #echo "获取第	$index 个输出配置 : 目标文件-> ${targetPath}  开始写入文件"
		writeToFile
		echo "根据第${index}个输出配置 : 目标文件-> ${targetPath}  结束写入文件"
		echo " "
		index=$[$index+1]
	fi
done






