from datetime import datetime, timedelta

from airflow import DAG
from airflow.providers.standard.operators.empty import EmptyOperator
from airflow.providers.standard.operators.bash import BashOperator

import os

DAG_DIR = os.path.dirname(os.path.abspath(__file__))

JAR_PATH = os.path.abspath(
    os.path.join(
        DAG_DIR,
        "../../../jars/opendota-metadata-scrapper-1.0-SNAPSHOT-all.jar"
    )
)
JAR_PATH = os.path.abspath(JAR_PATH)

default_args = {
    "owner": "Pablo Guerra",
    "depends_on_past": False,
    "retries": 1,
    "retry_delay": timedelta(minutes=5),
}

with DAG(
    dag_id="dota_ingestion_pipeline",
    description="Pipeline de extracción y procesamiento de partidas de Dota 2",
    start_date=datetime(2026, 6, 1),
    schedule=None,  # ejecución manual
    catchup=False,
    default_args=default_args,
    tags={"dota2", "telemetry", "java"},
) as dag:

    inicio = EmptyOperator(
        task_id="inicio"
    )

    metadata_ingestion = BashOperator(
        task_id="metadata_ingestion",
        bash_command=f"java -jar {JAR_PATH} 19101",
        env={
            "DB_URL": "jdbc:sqlserver://172.24.32.1:1433;databaseName=dota_db_parser;encrypt=false",
            "DB_USER": "dota_user",
            "DB_PASSWORD": "guerra2350"
        },
        do_xcom_push=True
    )

    debug_xcom = BashOperator(
        task_id="debug_xcom",
        bash_command="""
        echo 'XCOM={{ ti.xcom_pull(task_ids="metadata_ingestion") }}'
        """
    )

    replay_download = BashOperator(
        task_id="replay_download",
        bash_command="""
        JSON='{{ ti.xcom_pull(task_ids="metadata_ingestion") }}'

        REPLAY_URL=$(echo "$JSON" | jq -r '.replayUrl')

        mkdir -p /tmp/dota_replays

        FILE_NAME=$(basename "$REPLAY_URL")

        wget -O /tmp/dota_replays/$FILE_NAME "$REPLAY_URL"

        bunzip2 -f /tmp/dota_replays/$FILE_NAME

        echo "/tmp/dota_replays/${FILE_NAME%.bz2}"
        """,
        do_xcom_push=True
    )

    telemetry_parser = BashOperator(
        task_id="telemetry_parser",
        bash_command="""
            DEM_FILE='{{ ti.xcom_pull(task_ids="replay_download") }}'
    
            java -jar /mnt/c/.../telemetry-parser-1.0-SNAPSHOT.jar "$DEM_FILE"
            """
    )

    fin = EmptyOperator(
        task_id="fin"
    )


    inicio >> metadata_ingestion >> debug_xcom >> replay_download >> telemetry_parser >> fin