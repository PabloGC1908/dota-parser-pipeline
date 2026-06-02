from datetime import datetime, timedelta

from airflow import DAG
from airflow.providers.standard.operators.empty import EmptyOperator
from airflow.providers.standard.operators.bash import BashOperator

default_args = {
    "owner": "pablo",
    "depends_on_past": False,
    "retries": 1,
    "retry_delay": timedelta(minutes=5),
}

with DAG(
    dag_id="dota_ingestion_pipeline",
    description="Pipeline de extracción y procesamiento de partidas de Dota 2",
    start_date=datetime(2026, 1, 1),
    schedule=None,  # ejecución manual
    catchup=False,
    default_args=default_args,
    tags=["dota2", "telemetry", "java"],
) as dag:

    inicio = EmptyOperator(
        task_id="inicio"
    )

    metadata_ingestion = BashOperator(
        task_id="metadata_ingestion",
        bash_command="""
        java -jar \
        /opt/airflow/jars/opendota-metadata-scraper.jar
        """
    )

    replay_download = BashOperator(
        task_id="replay_download",
        bash_command="""
        /opt/airflow/jars/telemetry-parser.jar
        """
    )

    telemetry_parser = BashOperator(
        task_id="telemetry_parser",
        bash_command="""
        java -jar \
        /opt/airflow/jars/telemetry-parser.jar
        """
    )

    fin = EmptyOperator(
        task_id="fin"
    )


    inicio >> metadata_ingestion >> replay_download >> telemetry_parser >> fin