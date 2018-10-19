#!/usr/bin/env bash
#
#    Licensed to the Apache Software Foundation (ASF) under one or more
#    contributor license agreements.  See the NOTICE file distributed with
#    this work for additional information regarding copyright ownership.
#    The ASF licenses this file to You under the Apache License, Version 2.0
#    (the "License"); you may not use this file except in compliance with
#    the License.  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#

function usage {
    cat <<-END >&2
    USAGE: $0
        -k KARAF_SERVICE_PATH       # Karaf installation path
        -d KARAF_SERVICE_DATA       # Karaf data path (default to \${KARAF_SERVICE_PATH}/data)
        -c KARAF_SERVICE_CONF       # Karaf configuration file (default to \${KARAF_SERVICE_PATH/etc/\${KARAF_SERVICE_NAME}.conf
        -t KARAF_SERVICE_ETC        # Karaf etc path (default to \${KARAF_SERVICE_PATH/etc}
        -p KARAF_SERVICE_PIDFILE    # Karaf pid path (default to \${KARAF_SERVICE_DATA}/\${KARAF_SERVICE_NAME}.pid)
        -n KARAF_SERVICE_NAME       # Karaf service name (default karaf)
        -e KARAF_ENV                # Karaf environment variable (can be repeated)
        -u KARAF_SERVICE_USER       # Karaf user
        -g KARAF_SERVICE_GROUP      # Karaf group (default \${KARAF_SERVICE_USER)
        -l KARAF_SERVICE_LOG        # Karaf console log (default to \${KARAF_SERVICE_DATA}/log/\${KARAF_SERVICE_NAME}-console.log)
        -f KARAF_SERVICE_TEMPLATE   # Template file to use
        -x KARAF_SERVICE_EXECUTABLE # Karaf executable name (defaul karaf, should support daemon and stop commands)
        -h                          # this usage message
END
    exit
}

CONF_TEMPLATE="karaf-service-template.conf"
SYSTEMD_TEMPLATE="karaf-service-template.systemd"
SYSTEMD_TEMPLATE_INSTANCES="karaf-service-template.systemd-instances"
INIT_TEMPLATE="karaf-service-template.init"
INIT_REDHAT_TEMPLATE="karaf-service-template.init-redhat"
INIT_DEBIAN_TEMPLATE="karaf-service-template.init-debian"
SOLARIS_SMF_TEMPLATE="karaf-service-template.solaris-smf"

################################################################################
#
################################################################################

KARAF_ENV=()

while getopts k:d:c:p:n:u:g:l:t:e:f:x:h opt
do
    case $opt in
    k)  export KARAF_SERVICE_PATH="$OPTARG" ;;
    d)  export KARAF_SERVICE_DATA="$OPTARG" ;;
    c)  export KARAF_SERVICE_CONF="$OPTARG" ;;
    p)  export KARAF_SERVICE_PIDFILE="$OPTARG" ;;
    n)  export KARAF_SERVICE_NAME="$OPTARG" ;;
    u)  export KARAF_SERVICE_USER="$OPTARG" ;;
    g)  export KARAF_SERVICE_GROUP="$OPTARG" ;;
    l)  export KARAF_SERVICE_LOG="$OPTARG" ;;
    t)  export KARAF_SERVICE_ETC="$OPTARG" ;;
    f)  export KARAF_SERVICE_TEMPLATE="$OPTARG" ;;
    x)  export KARAF_SERVICE_EXECUTABLE="$OPTARG" ;;
    e)  KARAF_ENV+=("$OPTARG") ;;
    h|?) usage ;;
    esac
done

shift $(( $OPTIND - 1 ))

if [[ ! $KARAF_SERVICE_PATH ]]; then
    echo "Warning, KARAF_SERVICE_PATH is required"
    usage
fi

if [[ ! $KARAF_SERVICE_DATA ]]; then
    export KARAF_SERVICE_DATA="${KARAF_SERVICE_PATH}/data"
fi

if [[ ! $KARAF_SERVICE_ETC ]]; then
    export KARAF_SERVICE_ETC="${KARAF_SERVICE_PATH}/etc"
fi

if [[ ! $KARAF_SERVICE_NAME ]]; then
    export KARAF_SERVICE_NAME="karaf"
fi

if [[ ! $KARAF_SERVICE_CONF ]]; then
    export KARAF_SERVICE_CONF="${KARAF_SERVICE_PATH}/etc/${KARAF_SERVICE_NAME}.conf"
fi

if [[ ! $KARAF_SERVICE_PIDFILE ]]; then
    export KARAF_SERVICE_PIDFILE="${KARAF_SERVICE_DATA}/${KARAF_SERVICE_NAME}.pid"
fi

if [[ ! $KARAF_SERVICE_LOG ]]; then
    export KARAF_SERVICE_LOG="${KARAF_SERVICE_DATA}/log/${KARAF_SERVICE_NAME}-console.log"
fi

if [[ ! $KARAF_SERVICE_USER ]]; then
    export KARAF_SERVICE_USER="root"
fi

if [[ ! $KARAF_SERVICE_GROUP ]]; then
    export KARAF_SERVICE_GROUP="${KARAF_SERVICE_USER}"
fi

if [[ ! $KARAF_SERVICE_EXECUTABLE ]]; then
    export KARAF_SERVICE_EXECUTABLE="karaf"
fi

################################################################################
#
################################################################################

function generate_service_descriptor {
    echo "Writing service file \"$2\""
    perl -p -e 's/\$\{([^}]+)\}/defined $ENV{$1} ? $ENV{$1} : $&/eg' < "$1" > "$2"

    if [ $# -eq 4 ]; then
        echo "Writing service configuration file \"$4\""
        perl -p -e 's/\$\{([^}]+)\}/defined $ENV{$1} ? $ENV{$1} : $&/eg' < "$3" > "$4"

        for var in "${KARAF_ENV[@]}"; do
          echo "${var}" >> "$4"
        done
    fi
}

################################################################################
#
################################################################################

if [[ ! $KARAF_SERVICE_TEMPLATE ]]; then
    case $(uname | tr [:upper:] [:lower:]) in
        sunos)
            # add KARAF_ENV vars to envirioment
            for var in "${KARAF_ENV[@]}"; do
                export $var
            done

            # Default java path if not set
            if [[ ! $JAVA_HOME ]]; then
                export JAVA_HOME=/usr/java
            fi

            # escape spaces in path
            export KARAF_SERVICE_PATH="$(echo $KARAF_SERVICE_PATH | sed 's/ /\\ /g')"
            export KARAF_SERVICE_DATA="$(echo $KARAF_SERVICE_DATA | sed 's/ /\\ /g')"
            export KARAF_SERVICE_CONF="$(echo $KARAF_SERVICE_CONF | sed 's/ /\\ /g')"
            export KARAF_SERVICE_PIDFILE="$(echo $KARAF_SERVICE_PIDFILE | sed 's/ /\\ /g')"
            
            generate_service_descriptor \
                "$SOLARIS_SMF_TEMPLATE" \
                "${PWD}/${KARAF_SERVICE_NAME}.xml"
            ;;
        linux)
            if [ -d /run/systemd/system ]; then
                generate_service_descriptor \
                    "$SYSTEMD_TEMPLATE" \
                    "${PWD}/${KARAF_SERVICE_NAME}.service" \
                    "${CONF_TEMPLATE}" \
                    "${KARAF_SERVICE_CONF}"

                generate_service_descriptor \
                    "$SYSTEMD_TEMPLATE_INSTANCES" \
                    "${PWD}/${KARAF_SERVICE_NAME}@.service"

            elif [ -f /etc/redhat-release ]; then
                generate_service_descriptor \
                    "$INIT_REDHAT_TEMPLATE" \
                    "${PWD}/${KARAF_SERVICE_NAME}" \
                    "${CONF_TEMPLATE}" \
                    "${KARAF_SERVICE_CONF}"

                chmod 755 "${PWD}/${KARAF_SERVICE_NAME}"
            elif [ -f /etc/debian-release ] || [ -f /etc/debian_version ]; then
                generate_service_descriptor \
                    "$INIT_DEBIAN_TEMPLATE" \
                    "${PWD}/${KARAF_SERVICE_NAME}" \
                    "${CONF_TEMPLATE}" \
                    "${KARAF_SERVICE_CONF}"

                chmod 755 "${PWD}/${KARAF_SERVICE_NAME}"
            fi
            ;;
        *)
            generate_service_descriptor \
                "$INIT_TEMPLATE" \
                "${PWD}/${KARAF_SERVICE_NAME}" \
                "${CONF_TEMPLATE}" \
                "${KARAF_SERVICE_CONF}"

            chmod 755 "${PWD}/${KARAF_SERVICE_NAME}"
            ;;
    esac
else
    generate_service_descriptor \
        "$KARAF_SERVICE_TEMPLATE" \
        "${PWD}/${KARAF_SERVICE_NAME}" \
        "${CONF_TEMPLATE}" \
        "${KARAF_SERVICE_CONF}"
fi
