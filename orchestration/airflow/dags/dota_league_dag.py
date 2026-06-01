from airflow import DAG
from airflow.providers.standard.operators.bash import BashOperator
from airflow.operators.empty import EmptyOperator
from datetime import datetime, timedelta

default_args = {
    'owner': 'pablo',
    'depends_on_past': False,
    'start_date': datetime(2023, 10, 1),
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

with DAG(
        'dota_league_parser_dag',
        default_args=default_args,
        description='Extrae datos de partidas de Dota 2 usando un JAR en Java',
        catchup=False,
        tags=['dota2', 'etl', 'java']
) as dag:
    inicio = EmptyOperator(task_id='inicio')

    # Usamos Jinja templating ({{ params.league_id }}) para pasarle la liga.
    ejecutar_parser_java = BashOperator(
        task_id='ejecutar_jar_parser',
        bash_command='java -jar /opt/airflow/jars/dota-parser.jar {{ params.league_id }}',
        params={'league_id': '12345'},
    )

    # Borrar los archivos .dem descargados
    limpiar_archivos_dem = BashOperator(
        task_id='limpiar_archivos_temporales',
        bash_command='rm -f /ruta/donde/descargas/los/replays/*.dem',
    )

    fin = EmptyOperator(task_id='fin')

    inicio >> ejecutar_parser_java >> limpiar_archivos_dem >> fin