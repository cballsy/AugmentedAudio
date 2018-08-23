from flask import Flask
import json

app = Flask(__name__)


@app.route('/api/dummy')
def home():
    # initialise
    results = {}

    # set values in return body
    results['status'] = 'OK'
    return json.dumps(results)


# if this has been run, start the flask server
if __name__ == "__main__":
    app.run(debug=True)
