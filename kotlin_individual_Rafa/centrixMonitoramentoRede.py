import speedtest as st
import time
from mysql.connector import connect
import pymssql
from datetime import datetime

cnx = connect(user='root', password='38762', host='localhost', database='centrix')
speed_test = st.Speedtest()

sql_server_cnx = pymssql.connect(server='44.197.21.59', database='centrix', user='sa', password='centrix');

while(True):
    download = speed_test.download()
    download_mbs = round(download / (10**6), 2)
                
    upload = speed_test.upload()
    upload_mbs = round(upload / (10**6), 2)
    
    latencia = speed_test.results.ping
    
    data_e_hora_atuais = datetime.now()
    data_atual = data_e_hora_atuais.date()
    hora_atual = data_e_hora_atuais.time()
                
    bd = cnx.cursor()
    bdServer_cursor = sql_server_cnx.cursor()
                
    dados_DOWNLOAD_PC = [download_mbs, 5, 7, 1]

    add_leitura_DOWNLOAD = ("INSERT INTO Monitoramento"
                           "(Data_captura, Hora_captura, Dado_Capturado, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni)"
                           "VALUES (%s, %s, %s, %s, %s, %s)")
               
    bd.execute(add_leitura_DOWNLOAD, (data_atual, hora_atual, download_mbs, 5, 7, 1))
    bdServer_cursor.execute(add_leitura_DOWNLOAD, (str(data_atual), str(hora_atual), download_mbs, 5, 7, 1))
                
   
    dados_UPLOAD_PC = [upload_mbs, 6, 7, 1]

    add_leitura_UPLOAD = ("INSERT INTO Monitoramento"
                         "(Data_captura, Hora_captura, Dado_Capturado, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni)"
                         "VALUES (%s, %s, %s, %s, %s, %s)")
                
    bd.execute(add_leitura_UPLOAD, (data_atual, hora_atual, upload_mbs, 6, 7, 1))

    bdServer_cursor.execute(add_leitura_UPLOAD, (str(data_atual), str(hora_atual), upload_mbs, 6, 7, 1))
    
    dados_LATENCIA_PC = [latencia, 9, 7, 1]
    
    add_leitura_LATENCIA = ("INSERT INTO Monitoramento"
                          "(Data_captura, Hora_captura, Dado_Capturado, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni)"
                          "VALUES (%s, %s, %s, %s, %s, %s)")
                
    bd.execute(add_leitura_LATENCIA, (data_atual, hora_atual, upload_mbs, 9, 7, 1))
    
    bdServer_cursor.execute(add_leitura_LATENCIA, (str(data_atual), str(hora_atual), latencia, 9, 7, 1))
                
    cnx.commit()
    sql_server_cnx.commit()
    bdServer_cursor.close()

    time.sleep(20)
