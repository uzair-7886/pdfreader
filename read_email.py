import requests
import ast
import base64
import logging
import os
import requests
from tqdm import tqdm
import json
from datetime import datetime

logging.basicConfig(format='[%(asctime)s]:[%(levelname)s]:[%(filename)s %(lineno)d]:[%(funcName)s]:%(message)s')
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

url = "https://login.microsoftonline.com/29749ef6-3d61-4cba-9467-3ae7e46401ee/oauth2/v2.0/token"

payload = 'client_id=78dce21a-9baf-40ec-b789-39d8ca9dc5cb&scope=offline_access%20Mail.ReadWrite%20Mail.send&grant_type=refresh_token&client_secret=sAN8Q~farnfKGybifEdVCBgMH9RLInmL0F9LTdAx&refresh_token=0.AUEB9p50KWE9ukyUZzrn5GQB7hri3Hivm-xAt4k52MqdxctCARk.AgABAwEAAADW6jl31mB3T7ugrWTT8pFeAwDs_wUA9P8hES2IHnqEdFT6W27xdYrt8dT3mbel-TYNCTVgpQ26OcQ828KLNVKBihzopx8dahxhN2pSji71Vc6A2SCIPvnQQnWJBK5MGMOA7CCT5lNJaT6dwBfQXeXS8O6RTtZ0hvU-7tKmSQyxGlEgZQfpTuIjZXIuN-L8iGR06SBWkPa2AjL5n3xFLU2WualJRgDrmZ1bRm2-O8lPX8zFM98ePJAxAndZ1r7ybEPsRVe6U7CoNnr8afLeu1V4lBWV4zM06jC5fqi-exX2ZOMx0r8j6ZUsYd00JfMduWJOukGJVHJAI9V7_onLUCNnZMsoRsEJhaTzsXbZeBSTMk6pwXu8LHUf6yqb_mScNlRFb9UIy1qF1k74Zf-ysEajlPdCyvmxQtjD0Grusyjc8onE0On7x_gjz0_hO8Fhstufv88nYdkzHPfCOH8imxJOe2Bk_wJUN4FxTcEHvfxxTphZcdpk_pLll8NCxU6h_FLknnZ1oojJ-A1X5YQwzOTn8h4AdCpAgbPKiXQ2Ei0i6SSC7qbsKJvTJG_y_pYVK2OXg0U7rP6KE25O6HgjVMs0S3bf6cCzvwIwgOYKhii8rj7gwAct7Ewt2fem7XBoEud1FGi65LqC8V2AHwRbI6KHt1W78emoMKfyaLs2YAZjDavULnSUC8bIoZ_WSB7sP3gDjUzJYKUwk3t7eLwVxT3TL35tHQYCSzkVpRzKnatHx5xafXldmJnPPKPZSfFycbKUYpi0p22EruWt8R2qFrwRlYoeusn_ETIh7VEp3de0O2FP5N82B9Uj5pKLW9ojvoX6KB4TYpQFSv7pw_jD8joE-gDZLqwagENVanWo_VPfvkMjt8Gshctzsp48BjKwSJ33Gr4i7E17a56CVe6lDXxh30Sbszf0IU6Kxf0zDJ3SvH107_LkaQfMxB8oER50R1WPw4EDFUCjdH226ozWzZGvOxTCAaUdMsZjdL0k9XDGOQawO-9kq_qdLlgpEoa2lOifMsPAHBMGVx9hiiASD8TQ5dlqXLEp_KJxhsV2yi1FtAvaUXC8yxHtj6AN27sbVzRgyydOkwJryylzzwibDXSapk0zVU1sfC139kHO54y6DE6fPXln-DdlPGXK82BEhsIKoyALnNdkuWftLq-ma6EUozi7eUnua9x41YTyLYOgyjP1XpdJDyA_prKN7ciIUTMbMZoT4QfzSrSb7eL0y9kijX6Hzwis0XJI8kDX7K-pnsIqScIg8FBrYJyk-tBYXZ7-5biQbNLU77gFWwYDgXXDgi5YoWhAL9UVmFA7k_GlonmJO_Un5_l4tcNsqx0o5DX0_f6YBEp7ESZVmgu91yv88_sn6-4hze_ophkQdemH196Af5jB4tij2wanw1BmLzyeYjS-Nql-BOMDZD8p1jc5k2d5gOQ4Gg1_ZCU3YtKSIPFWmXKHMYUQEOyJvuIv6sGjQwxf5F5qYUaVU8li15wPR3zIAbCstaLkIpTPRN2CigDoaDQ55o2Bhq0Xxxq5DbUTU-F4gaCcc34MpajY6FEa6o7GoK5ftyZc0SFvChOS'
headers = {
  'Content-Type': 'application/x-www-form-urlencoded',
  'Cookie': 'fpc=AmHlpEBkeTZGjtV6wbq9akHZAZz9AQAAAC6jod4OAAAA; stsservicecookie=estsfd; x-ms-gateway-slice=estsfd'
}

response = requests.request("POST", url, headers=headers, data=payload)

print(response.text)


try:
    response = requests.post(url, headers=headers, data=payload)
    response.raise_for_status()  

    # response = requests.request("POST", url, headers=headers, data=payload)

    result = ast.literal_eval(str(response.text))
    # print(result)

except requests.exceptions.RequestException as e:
    logger.exception("An error occurred while requesting the token")


def mail_read(result):
    if "access_token" not in result:
        print("Error: No access token found in the result dictionary")
        return

    subject = "Test email"
    endpoint = 'https://graph.microsoft.com/v1.0/me/messages'
    params = {
        "$search": f'"subject:{subject}"',
        "$top": 1,
        "$select": "subject,bodyPreview,body,receivedDateTime"
    }
    headers = {
        'Authorization': f'Bearer {result["access_token"]}',
        'Accept': 'application/json',
        'Prefer': 'outlook.body-content-type="text"'
    }

    print(f"--- Debug: mail_read function started at {datetime.now()} ---")
    print(f"Endpoint: {endpoint}")
    print(f"Search parameters: {json.dumps(params, indent=2)}")
    print(f"Headers: {json.dumps({k: v if k != 'Authorization' else 'Bearer [REDACTED]' for k, v in headers.items()}, indent=2)}")

    try:
        print("Sending GET request to Microsoft Graph API...")
        r = requests.get(endpoint, headers=headers, params=params)
        print(f"Response status code: {r.status_code}")
        print(f"Response headers: {json.dumps(dict(r.headers), indent=2)}")

        r.raise_for_status()

        print("Parsing JSON response...")
        data = r.json()
        print(f"Response data: {json.dumps(data, indent=2)}")

        if "value" in data and len(data["value"]) > 0:
            email = data["value"][0]
            print("\nEmail found:")
            print(f"Subject: {email['subject']}")
            print(f"Received: {email['receivedDateTime']}")
            print(f"Body Preview: {email['bodyPreview']}")
            print("Full Body Content:")
            print(email['body']['content'])
        else:
            print("No emails found matching the search criteria")

    except requests.exceptions.RequestException as e:
        print(f"An error occurred while retrieving emails: {str(e)}")
        if hasattr(r, 'text'):
            print(f"Error response content: {r.text}")
    except json.JSONDecodeError:
        print("Error: Unable to parse the response as JSON")
        print(f"Raw response content: {r.text}")
    except Exception as e:
        print(f"An unexpected error occurred: {str(e)}")

    print(f"--- Debug: mail_read function ended at {datetime.now()} ---")


mail_read(result)


